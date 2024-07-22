package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException; //check filepath
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import response.LoginResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoginServiceTest {
    private LoginService loginService;
    private static UserDAO userDAO;
    private static AuthDAO authDAO;

    @BeforeAll
    public static void setUpAll() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        loginService = new LoginService(userDAO, authDAO);
        // Add a test user before each test
        userDAO.addUser(new UserData("testUser", "password", "test@example.com"));
    }

    @Test
    public void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("testUser", "password");
        LoginResult result = loginService.login(request);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("testUser", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void testLoginFailure() {
        LoginRequest request = new LoginRequest("testUser", "wrongPassword");
        Assertions.assertThrows(Exception.class, () -> {
            loginService.login(request);
        });
    }
}
