package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import model.AuthData;
import model.UserData;
import request.RegisterRequest;
import response.RegisterResult;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws Exception {
        UserData user = new UserData(request.getUsername(), request.getPassword(), request.getEmail());
        if (userDAO.addUser(user)) {
            String authToken = generateAuthToken();
            AuthData authData = new AuthData(authToken, user.getUsername());
            authDAO.addAuthData(authData);
            System.out.println("User registered: " + user.getUsername());
            return new RegisterResult(user.getUsername(), authToken);
        } else {
            System.out.println("User already exists: " + user.getUsername());
            throw new DataAccessException("User already exists");  // Ensure this exception is thrown
        }
    }

    private String generateAuthToken() {
        return java.util.UUID.randomUUID().toString();
    }
}

