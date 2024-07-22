package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException; // Import this
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClearServiceTest {
    private ClearService clearService;
    private static UserDAO userDAO;
    private static GameDAO gameDAO;
    private static AuthDAO authDAO;

    @BeforeAll
    public static void setUpAll() {
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
    }

    @BeforeEach
    public void setUp() {
        clearService = new ClearService(userDAO, gameDAO, authDAO);
    }

    @Test
    public void testClearSuccess() throws DataAccessException {
        clearService.clear();
        // Verify that all data has been cleared
        Assertions.assertTrue(userDAO.getAllUsers().isEmpty());
        Assertions.assertTrue(gameDAO.getAllGames().isEmpty());
        Assertions.assertTrue(authDAO.getAllAuthData().isEmpty());
    }
}
