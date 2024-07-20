package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private Map<String, AuthData> authDataMap = new HashMap<>();

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        return authDataMap.get(authToken);
    }

    @Override
    public boolean addAuthData(AuthData authData) throws DataAccessException {
        if (authDataMap.containsKey(authData.getAuthToken())) {
            throw new DataAccessException("Auth token already exists");
        }
        authDataMap.put(authData.getAuthToken(), authData);
        return true;
    }

    @Override
    public boolean deleteAuthData(String authToken) throws DataAccessException {
        if (!authDataMap.containsKey(authToken)) {
            throw new DataAccessException("Auth token does not exist");
        }
        authDataMap.remove(authToken);
        return true;
    }
}
