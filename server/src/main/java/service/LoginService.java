package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
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
        if (request.username() == null || request.password() == null) {
            throw new DataAccessException("Error: Missing required fields");
        }

        // Retrieve user data
        UserData user = userDAO.getUser(request.username());
        if (user == null || !verifyPassword(request.password(), user.getPassword())) {
            throw new DataAccessException("Error: Invalid username or password");
        }

        // Generate an auth token for the user
        String authToken = authDAO.generateAuthToken(user.getUsername());
        // Create auth data and add it to the data store
        AuthData authData = new AuthData(authToken, user.getUsername());
        authDAO.addAuthData(authData);

        // Return the login result
        return new LoginResult(user.getUsername(), authToken);
    }

    private boolean verifyPassword(String providedPassword, String storedHashedPassword) {
        return BCrypt.checkpw(providedPassword, storedHashedPassword);
    }
}
