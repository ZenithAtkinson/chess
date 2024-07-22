package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import response.ListGamesResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ListGamesServiceTest {
    private ListGamesService listGamesService;
    private static GameDAO gameDAO;
    private static AuthDAO authDAO;

    @BeforeAll
    public static void setUpAll() {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        listGamesService = new ListGamesService(gameDAO, authDAO);

        // Add a valid authToken to the authDAO
        AuthData authData = new AuthData("authToken", "testUser");
        authDAO.addAuthData(authData);

        // Add a game to the gameDAO
        GameData gameData = new GameData(1, "testUser", null, "testGame", null);
        gameDAO.addGame(gameData);
    }

    @Test
    public void testListGamesSuccess() throws Exception {
        ListGamesResult result = listGamesService.listGames("authToken");
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.games().size() >= 0); // Should return an empty list or more
    }

    @Test
    public void testListGamesFailure() {
        Assertions.assertThrows(Exception.class, () -> {
            listGamesService.listGames("invalidAuthToken");
        });
    }
}
