package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;

public class LogoutService {
    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void logout(String authToken) throws DataAccessException {
        if (authDAO.getAuthData(authToken) == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        authDAO.deleteAuthData(authToken);
    }
}
