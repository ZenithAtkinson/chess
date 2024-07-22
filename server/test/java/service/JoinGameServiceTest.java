package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import request.JoinGameRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JoinGameServiceTest {
    private JoinGameService joinGameService;
    private static GameDAO gameDAO;
    private static AuthDAO authDAO;

    @BeforeAll
    public static void setUpAll() {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        joinGameService = new JoinGameService(gameDAO, authDAO);

        // Add a valid authToken to the authDAO
        AuthData authData = new AuthData("authToken", "testUser");
        authDAO.addAuthData(authData);

        // Add a game to the gameDAO
        GameData gameData = new GameData(1, null, null, "newGame", null);
        gameDAO.addGame(gameData);
    }

    @Test
    public void testJoinGameSuccess() throws Exception {
        // Assuming there's a game with ID 1
        JoinGameRequest request = new JoinGameRequest(1, "WHITE");
        joinGameService.joinGame(request, "authToken");
        // Add assertions as needed
    }

    @Test
    public void testJoinGameFailure() {
        JoinGameRequest request = new JoinGameRequest(-1, "WHITE"); // Invalid game ID
        Assertions.assertThrows(Exception.class, () -> {
            joinGameService.joinGame(request, "authToken");
        });
    }
}
