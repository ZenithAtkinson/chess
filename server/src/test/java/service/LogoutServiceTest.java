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
    }

    @Test
    public void logoutPass() throws Exception {
        //Add auth token
        AuthData authData = new AuthData("authToken", "testUser");
        authDAO.addAuthData(authData);

        logoutService.logout("authToken");

        //Verify  auth token was removed
        AuthData removedAuthData = authDAO.getAuthData("authToken");
        Assertions.assertNull(removedAuthData);
    }

    @Test
    public void logoutFail() {
        Assertions.assertThrows(DataAccessException.class, () -> {
            logoutService.logout("invalidAuthToken");
        });
    }
}
