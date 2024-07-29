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

    // LOGIN process
    public LoginResult login(LoginRequest request) throws DataAccessException {
        // Get the user data from store
        UserData user = userDAO.getUser(request.username());
        // Validate credentials
        if (user != null && BCrypt.checkpw(request.password(), user.getPassword())) {
            // Generate auth token
            String authToken = generateAuthToken();
            // Create auth data and add it to the data store
            AuthData authData = new AuthData(authToken, user.getUsername());
            authDAO.addAuthData(authData);
            // Return login result
            return new LoginResult(user.getUsername(), authToken);
        } else {
            throw new DataAccessException("Error: invalid username or password"); // Throw exception if credentials are invalid
        }
    }

    // Unique token from randomUUID
    private String generateAuthToken() {
        return java.util.UUID.randomUUID().toString();
    }
}
