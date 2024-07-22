package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LogoutServiceTest {
    private LogoutService logoutService;
    private static AuthDAO authDAO;

    @BeforeAll
    public static void setUpAll() {
        authDAO = new MemoryAuthDAO();
    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        logoutService = new LogoutService(authDAO);

        // Add a valid authToken to the authDAO
        AuthData authData = new AuthData("authToken", "testUser");
        authDAO.addAuthData(authData);
    }

    @Test
    public void testLogoutSuccess() throws Exception {
        // Assuming auth token "authToken" exists
        logoutService.logout("authToken");
        // Verify that the authToken has been removed
        Assertions.assertNull(authDAO.getAuthData("authToken"));
    }

    @Test
    public void testLogoutFailure() {
        Assertions.assertThrows(Exception.class, () -> {
            logoutService.logout("invalidAuthToken");
        });
    }
}
