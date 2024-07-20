package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData getAuthData(String authToken) throws DataAccessException;
    boolean addAuthData(AuthData authData) throws DataAccessException;
    boolean deleteAuthData(String authToken) throws DataAccessException;
}
