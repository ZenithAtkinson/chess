package service;

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
        UserData user = new UserData(request.username(), request.password(), request.email());
        if (userDAO.addUser(user)) {
            String authToken = generateAuthToken();
            AuthData authData = new AuthData(authToken, user.getUsername());
            authDAO.addAuthData(authData);
            return new RegisterResult(user.getUsername(), authToken);
        } else {
            throw new Exception("User already exists");
        }
    }

    private String generateAuthToken() {
        return java.util.UUID.randomUUID().toString();
    }
}
