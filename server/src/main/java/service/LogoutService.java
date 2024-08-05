package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;

public class LogoutService {
    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    /**
     * Logs out the user by deleting their authentication token.
     *
     * @param authToken The authentication token to be invalidated.
     * @throws DataAccessException If the token is invalid or not found.
     */
    public void logout(String authToken) throws DataAccessException {
        // Remove the "Bearer " prefix if present
        if (authToken != null && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
        }

        // Check if the token is valid
        if (authDAO.getAuthData(authToken) == null) {
            throw new DataAccessException("Error: Unauthorized - Invalid token");
        }

        // Delete the authentication data
        authDAO.deleteAuthData(authToken);
    }
}
