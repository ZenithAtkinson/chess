package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.RegisterRequest;
import response.RegisterResult;

public class RegisterService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if (request.getUsername() == null || request.getPassword() == null || request.getEmail() == null) {
            throw new DataAccessException("Error: Missing required fields");
        }
        // Check if user exists
        UserData existingUser = userDAO.getUser(request.getUsername());
        if (existingUser != null) {
            throw new DataAccessException("Error: User already exists");
        }

        // Hash the password
        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        // Create the new user with the hashed password
        UserData newUser = new UserData(request.getUsername(), hashedPassword, request.getEmail());
        if (userDAO.addUser(newUser)) {
            // Generate an auth token for the new user
            String authToken = authDAO.generateAuthToken(newUser.getUsername());
            // Create auth data and add it to the data store
            AuthData authData = new AuthData(authToken, newUser.getUsername());
            authDAO.addAuthData(authData);
            // Return the registration result
            return new RegisterResult(newUser.getUsername(), authToken);
        } else {
            throw new DataAccessException("Error: Failed to register user");
        }
    }
}
