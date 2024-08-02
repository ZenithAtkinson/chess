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

        UserData existingUser = userDAO.getUser(request.getUsername());
        if (existingUser != null) {
            throw new DataAccessException("Error: User already exists");
        }

        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        UserData newUser = new UserData(request.getUsername(), hashedPassword, request.getEmail());

        if (userDAO.addUser(newUser)) {
            String authToken = authDAO.generateAuthToken(newUser.getUsername());
            AuthData authData = new AuthData(authToken, newUser.getUsername());
            authDAO.addAuthData(authData);
            return new RegisterResult(newUser.getUsername(), authToken);
        } else {
            throw new DataAccessException("Error: Failed to register user");
        }
    }
}
