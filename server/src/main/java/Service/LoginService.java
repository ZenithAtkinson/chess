package Service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData login(UserData request) throws DataAccessException {
        UserData user = userDAO.getUser(request.getUsername());
        if (user != null && user.getPassword().equals(request.getPassword())) {
            String authToken = generateAuthToken();
            AuthData authData = new AuthData(authToken, user.getUsername());
            authDAO.addAuthData(authData);
            return authData;
        } else {
            throw new DataAccessException("Error: invalid username or password");
        }
    }

    private String generateAuthToken() {
        return java.util.UUID.randomUUID().toString();
    }
}
