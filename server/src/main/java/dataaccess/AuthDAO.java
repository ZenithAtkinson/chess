package dataaccess;

import model.AuthData;
import java.util.List;

public interface AuthDAO {
    AuthData getAuthData(String authToken) throws DataAccessException;
    boolean addAuthData(AuthData authData) throws DataAccessException; //return boolean or void?
    boolean deleteAuthData(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
    List<AuthData> getAllAuthData() throws DataAccessException; // needs list method
}



