package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import response.ListGamesResult;
import model.AuthData;
import model.GameData;
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
    }

    @Test
    public void listGamesPass() throws Exception {
        //Add auth token
        AuthData authData = new AuthData("authToken", "testUser");
        authDAO.addAuthData(authData);

        //Add game
        GameData gameData = new GameData(1, "testUser", null, "testGame", null);
        gameDAO.addGame(gameData);

        ListGamesResult result = listGamesService.listGames("authToken");
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.games().size() >= 0);
    }

    @Test
    public void listGamesFail() {
        Assertions.assertThrows(DataAccessException.class, () -> {
            listGamesService.listGames("invalidAuthToken");
        });
    }
}
