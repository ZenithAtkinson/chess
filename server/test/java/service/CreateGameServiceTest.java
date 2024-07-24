package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
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

    @BeforeAll
    public static void setUpAll() {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        createGameService = new CreateGameService(gameDAO, authDAO);
    }

    @Test
    public void createGamePass() throws Exception {
        CreateGameRequest request = new CreateGameRequest("testGame");
        CreateGameResult result = createGameService.createGame(request, "authToken");
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getGameID() > 0);
    }

    @Test
    public void createGameFailure() {
        CreateGameRequest request = new CreateGameRequest(null); //Invalid request with null game name
        Assertions.assertThrows(DataAccessException.class, () -> {
            createGameService.createGame(request, "authToken");
        });
    }
}
