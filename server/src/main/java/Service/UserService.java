package Service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) throws Exception {
        if (userDAO.addUser(user)) {
            String authToken = generateAuthToken();
            AuthData authData = new AuthData(authToken, user.getUsername());
            authDAO.addAuthData(authData);
            return authData;
        } else {
            throw new Exception("User already exists");
        }
    }

    private String generateAuthToken() {
        return java.util.UUID.randomUUID().toString();
    }
}
