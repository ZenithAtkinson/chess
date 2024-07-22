package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import response.CreateGameResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CreateGameServiceTest {
    private CreateGameService createGameService;
    private static GameDAO gameDAO;
    private static AuthDAO authDAO;
    private static final String AUTH_TOKEN = "authToken";

    @BeforeAll
    public static void setUpAll() {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        createGameService = new CreateGameService(gameDAO, authDAO);
        authDAO.addAuthData(new AuthData(AUTH_TOKEN, "testUser"));
    }

    @Test
    public void testCreateGameSuccess() throws Exception {
        CreateGameRequest request = new CreateGameRequest("newGame");
        CreateGameResult result = createGameService.createGame(request, AUTH_TOKEN);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getGameID() > 0);
    }

    @Test
    public void testCreateGameFailure() {
        CreateGameRequest request = new CreateGameRequest(null); // Invalid request with null game name
        Assertions.assertThrows(Exception.class, () -> {
            createGameService.createGame(request, AUTH_TOKEN);
        });
    }
}
