package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException; //check file path
import model.UserData;
import request.RegisterRequest;
import response.RegisterResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegisterServiceTest {
    private RegisterService registerService;
    private static UserDAO userDAO;
    private static AuthDAO authDAO;

    @BeforeAll
    public static void setUpAll() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        registerService = new RegisterService(userDAO, authDAO);
        // Clear any existing users for each test to start with a clean state
        userDAO.clear();
    }

    @Test
    public void registerPass() throws Exception {
        RegisterRequest request = new RegisterRequest("testUser", "password", "email@mail.com");
        RegisterResult result = registerService.register(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("testUser", result.getUsername());
        Assertions.assertNotNull(result.getAuthToken());
    }

    @Test
    public void registerFail() throws Exception {
        // Add a user first
        UserData user = new UserData("testUser", "password", "email@mail.com");
        userDAO.addUser(user);

        RegisterRequest request = new RegisterRequest("testUser", "password", "email@mail.com");
        Assertions.assertThrows(DataAccessException.class, () -> {
            registerService.register(request);
        });
    }
}
