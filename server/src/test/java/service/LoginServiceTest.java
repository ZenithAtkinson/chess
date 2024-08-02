package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.DataAccessException;
import model.UserData;
import request.LoginRequest;
import response.LoginResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {
    private LoginService loginService;
    private static UserDAO userDAO;
    private static AuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        loginService = new LoginService(userDAO, authDAO);

        // Register user for login test
        String hashedPassword = BCrypt.hashpw("password", BCrypt.gensalt());
        UserData user = new UserData("testUser", hashedPassword, "email@example.com");
        userDAO.addUser(user);
    }

    @Test
    public void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("testUser", "password");
        LoginResult result = loginService.login(request);
        assertNotNull(result);
        assertEquals("testUser", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void testLoginFailure() {
        LoginRequest request = new LoginRequest("testUser", "wrongpassword");
        assertThrows(DataAccessException.class, () -> loginService.login(request));
    }
}
