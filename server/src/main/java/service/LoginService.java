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

        if (!verifyUser(request.username(), request.password())) {
            throw new DataAccessException("Error: Invalid username or password");
        }

        // Generate an auth token for the user
        String authToken = authDAO.generateAuthToken(request.username());
        // Create auth data and add it to the data store
        AuthData authData = new AuthData(authToken, request.username());
        authDAO.addAuthData(authData);

        // Return the login result
        return new LoginResult(request.username(), authToken);
    }

    private boolean verifyUser(String username, String providedClearTextPassword) throws DataAccessException {
        //Retrieve user data
        UserData user = userDAO.getUser(username);
        if (user == null) {
            return false;
        }

        //Read the previously hashed password from the database
        String hashedPassword = user.getPassword();

        //Compare the provided password with the stored hashed password
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }
}
