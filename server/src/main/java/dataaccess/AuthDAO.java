package dataaccess;

import model.AuthData;

import java.util.Collection;
import java.util.List;

public interface AuthDAO {
    AuthData getAuthData(String authToken) throws DataAccessException;
    boolean addAuthData(AuthData authData) throws DataAccessException;
    boolean deleteAuthData(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
    Collection<AuthData> getAllAuthData() throws DataAccessException;
    String generateAuthToken(String username) throws DataAccessException;
}
