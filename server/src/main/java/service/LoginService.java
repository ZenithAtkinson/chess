package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import response.LoginResult;

public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        UserData user = userDAO.getUser(request.username());
        if (user != null && user.getPassword().equals(request.password())) {
            String authToken = generateAuthToken();
            AuthData authData = new AuthData(authToken, user.getUsername());
            authDAO.addAuthData(authData);
            return new LoginResult(user.getUsername(), authToken);
        } else {
            throw new DataAccessException("Error: invalid username or password");
        }
    }

    private String generateAuthToken() {
        return java.util.UUID.randomUUID().toString();
    }
}


