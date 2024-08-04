package client;

import ServerUtils.ServerFacade;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import server.Server;
import model.AuthData;
import model.GameData;
import model.UserData;
import request.CreateGameRequest;

import java.util.Arrays;

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
    void listGames() throws Exception {
        UserData registerRequest = new UserData("player4", "password", "p4@email.com");
        facade.register(registerRequest);

        UserData loginRequest = new UserData("player4", "password", null);
        AuthData authData = facade.login(loginRequest);
        facade.setAuthData(authData);

        CreateGameRequest gameData = new CreateGameRequest("game1");
        facade.createGame(gameData);

        gameData = new CreateGameRequest("game2");
        facade.createGame(gameData);

        System.out.println("Created GameData: " + gameData);

        GameData[] response = facade.listGames();
        System.out.println("List Games Test - GameData Array: " + response);
        assertNotNull(response);
        assertTrue(response.length > 0);
    }
}
