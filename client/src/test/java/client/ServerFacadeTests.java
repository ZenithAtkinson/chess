package client;

import ServerUtils.ServerFacade;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import request.JoinGameRequest;
import server.Server;
import model.AuthData;
import model.GameData;
import model.UserData;
import request.CreateGameRequest;

import java.util.Arrays;
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

    @BeforeEach
    public void clearDatabase() {
        try {
            facade.clearDatabase();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    @Test
    void register() throws Exception {
        UserData request = new UserData("player1", "password", "p1@email.com");
        AuthData authData = facade.register(request);
        System.out.println("Register Test - AuthData: " + authData);
        assertNotNull(authData);
        assertTrue(authData.getAuthToken().length() > 10);
    }

    @Test
    void registerDuplicateUser() {
        assertThrows(Exception.class, () -> {
            UserData request = new UserData("player1", "password", "p1@email.com");
            facade.register(request);
            facade.register(request);
        });
    }

    @Test
    void login() throws Exception {
        UserData registerRequest = new UserData("player2", "password", "p2@email.com");
        facade.register(registerRequest);

        UserData loginRequest = new UserData("player2", "password", null);
        AuthData response = facade.login(loginRequest);
        System.out.println("Login Test - AuthData: " + response);
        assertNotNull(response.getAuthToken());
        assertTrue(response.getAuthToken().length() > 10);
    }

    @Test
    void loginWithInvalidCredentials() {
        assertThrows(Exception.class, () -> {
            UserData loginRequest = new UserData("invalidUser", "invalidPassword", null);
            facade.login(loginRequest);
        });
    }

    @Test
    void createGame() throws Exception {
        UserData registerRequest = new UserData("player3", "password", "p3@email.com");
        facade.register(registerRequest);

        UserData loginRequest = new UserData("player3", "password", null);
        AuthData authData = facade.login(loginRequest);
        facade.setAuthData(authData);

        CreateGameRequest gameData = new CreateGameRequest("game1");
        GameData response = facade.createGame(gameData);
        System.out.println("Create Game Test - GameData: " + response);
        assertNotNull(response);
    }

    @Test
    void createGameWithoutLogin() {
        assertThrows(Exception.class, () -> {
            CreateGameRequest gameData = new CreateGameRequest("gameWithoutLogin");
            facade.createGame(gameData);
        });
    }

    @Test
    void listGames() throws Exception {
        UserData registerRequest = new UserData("player4", "password", "p4@email.com");
        facade.register(registerRequest);

        UserData loginRequest = new UserData("player4", "password", null);
        AuthData authData = facade.login(loginRequest);
        facade.setAuthData(authData);

        CreateGameRequest gameData1 = new CreateGameRequest("game1");
        facade.createGame(gameData1);

        CreateGameRequest gameData2 = new CreateGameRequest("game2");
        facade.createGame(gameData2);

        List<GameData> response = facade.listGames();
        System.out.println("List Games Test - GameData Array: " + response);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    void listGamesWithoutLogin() {
        assertThrows(Exception.class, () -> {
            facade.setAuthData(null);
            facade.listGames();
        });
    }

    @Test
    void joinGame() throws Exception {
        UserData registerRequest = new UserData("player5", "password", "p5@email.com");
        facade.register(registerRequest);

        UserData loginRequest = new UserData("player5", "password", null);
        AuthData authData = facade.login(loginRequest);
        facade.setAuthData(authData);

        CreateGameRequest gameData = new CreateGameRequest("gameToJoin");
        GameData createdGame = facade.createGame(gameData);

        JoinGameRequest joinRequest = new JoinGameRequest(createdGame.getGameID(), "WHITE");
        facade.joinGame(joinRequest);

        List<GameData> games = facade.listGames();
        assertTrue(games.stream().anyMatch(game -> game.getGameID() == createdGame.getGameID() && "player5".equals(game.getWhiteUsername())));
    }

    @Test
    void joinGameWithoutLogin() {
        assertThrows(Exception.class, () -> {
            JoinGameRequest joinRequest = new JoinGameRequest(999, "WHITE");
            facade.joinGame(joinRequest);
        });
    }

}
