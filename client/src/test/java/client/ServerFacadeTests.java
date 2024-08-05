package client;

import serverutils.ServerFacade;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import request.JoinGameRequest;
import server.Server;
import model.AuthData;
import model.GameData;
import model.UserData;
import request.CreateGameRequest;
import ui.BoardPrinter;
import chess.ChessBoard;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    // I need to clear the database constantly to avoid any strange and unexpected errors in the "pass" tests.
    @BeforeEach
    public void clearDatabase() {
        try {
            facade.clearDatabase();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    @Test //pass
    void registerPass() throws Exception {
        UserData request = new UserData("player1", "yourmom", "p1@email.com");
        AuthData authData = facade.register(request);
        //System.out.println("Register Test - AuthData: " + authData);
        assertNotNull(authData);
        assertTrue(authData.getAuthToken().length() > 10);
    }

    @Test //fail (diplicate user)
    void registerFail() {
        assertThrows(Exception.class, () -> {
            UserData request = new UserData("player1", "yourmom", "p1@email.com");
            facade.register(request);
            facade.register(request);
        });
    }

    @Test //pass
    void loginPass() throws Exception {
        UserData registerRequest = new UserData("player2", "yourmom", "p2@email.com");
        facade.register(registerRequest);

        UserData loginRequest = new UserData("player2", "yourmom", null);
        AuthData response = facade.login(loginRequest);
        //System.out.println("Login Test - AuthData: " + response);
        assertNotNull(response.getAuthToken());
        assertTrue(response.getAuthToken().length() > 10);
    }

    @Test //fail (WithInvalidCredentials)
    void loginFail() {
        assertThrows(Exception.class, () -> {
            UserData loginRequest = new UserData("CheaterLemonEater", "1234", null);
            facade.login(loginRequest);
        });
    }

    @Test //pass
    void createGamePass() throws Exception {
        UserData registerRequest = new UserData("player3", "yourmom", "p3@email.com");
        facade.register(registerRequest);

        UserData loginRequest = new UserData("player3", "yourmom", null);
        AuthData authData = facade.login(loginRequest);
        facade.setAuthData(authData);

        CreateGameRequest gameData = new CreateGameRequest("game1");
        GameData response = facade.createGame(gameData);
        //System.out.println("Create Game Test - GameData: " + response);
        assertNotNull(response);
    }

    @Test //fail (WithoutLogin)
    void createGameFail() {
        assertThrows(Exception.class, () -> {
            CreateGameRequest gameData = new CreateGameRequest("gameWithoutLogin");
            facade.createGame(gameData);
        });
    }

    @Test //pass
    void listGamesPass() throws Exception {
        UserData registerRequest = new UserData("player4", "yourmom", "p4@email.com");
        facade.register(registerRequest);

        UserData loginRequest = new UserData("player4", "yourmom", null);
        AuthData authData = facade.login(loginRequest);
        facade.setAuthData(authData);

        CreateGameRequest gameData1 = new CreateGameRequest("game1");
        facade.createGame(gameData1);

        CreateGameRequest gameData2 = new CreateGameRequest("game2");
        facade.createGame(gameData2);

        List<GameData> response = facade.listGames();
        //System.out.println("List Games Test - GameData Array: " + response);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test //fail (WithoutLogin)
    void listGamesFail() {
        //Not logged in...
        assertThrows(Exception.class, () -> {
            //facade.setAuthData(null);
            facade.listGames();
        });
    }

    @Test //pass
    void joinGamePass() throws Exception {
        UserData registerRequest = new UserData("player5", "yourmom", "p5@email.com");
        facade.register(registerRequest);

        UserData loginRequest = new UserData("player5", "yourmom", null);
        AuthData authData = facade.login(loginRequest);
        facade.setAuthData(authData);

        CreateGameRequest gameData = new CreateGameRequest("gameToJoin");
        GameData createdGame = facade.createGame(gameData);

        JoinGameRequest joinRequest = new JoinGameRequest(createdGame.getGameID(), "WHITE");
        facade.joinGame(joinRequest);

        List<GameData> games = facade.listGames();
        assertTrue(games.stream().anyMatch(game -> game.getGameID() == createdGame.getGameID() && "player5".equals(game.getWhiteUsername())));
    }

    @Test //fail (WithoutLogin)
    void joinGameFail() {
        assertThrows(Exception.class, () -> {
            JoinGameRequest joinRequest = new JoinGameRequest(69, "WHITE");
            //69 is non existent game... results in failure
            facade.joinGame(joinRequest);
        });
    }

    @Test //pass
    void logoutPass() throws Exception {

        UserData registerRequest = new UserData("player6", "yourmom", "p6@email.com");
        AuthData authData = facade.register(registerRequest);
        facade.setAuthData(authData);

        //Erroring here
        facade.logout();

        //Try to list games after logout, should throw exception as the user is logged out
        assertThrows(Exception.class, () -> {
            facade.listGames();
        });
    }

    @Test //fail (WithoutLogin)
    void logoutFail() {
        // Perform logout without logging in
        assertThrows(Exception.class, () -> {
            facade.logout();
        });
    }

    @Test
    void testBoardPrinter() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        BoardPrinter boardPrinter = new BoardPrinter();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream originalOut = System.out;

        System.setOut(printStream);

        // Print for WHITE
        boardPrinter.printBoard(board);
        // Print for BLAC
        boardPrinter.printBoardReversed(board);

        System.out.flush();
        System.setOut(originalOut);

        String boardOutput = outputStream.toString();
        assertNotNull(boardOutput);
        assertFalse(boardOutput.isEmpty());

        System.out.println("Board Printer Test Output:");
        System.out.println(boardOutput);
    }
}
