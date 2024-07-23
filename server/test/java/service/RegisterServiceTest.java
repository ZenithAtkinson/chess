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
    private UserService userService;
    private static UserDAO userDAO;
    private static AuthDAO authDAO;

    @BeforeAll
    public static void setUpAll() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        userService = new UserService(userDAO, authDAO);
    }
/*
    @Test
    public void testRegisterSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("newUser", "password", "new@example.com");
        RegisterResult result = userService.register(request);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("newUser", result.username());
        Assertions.assertNotNull(result.authToken());
    }*/

    /*@Test
    public void testRegisterFailure() throws DataAccessException {
        userDAO.addUser(new UserData("existingUser", "password", "existing@example.com"));
        RegisterRequest request = new RegisterRequest("existingUser", "password", "existing@example.com");
        Assertions.assertThrows(Exception.class, () -> {
            userService.register(request);
        });
    }*/
}
