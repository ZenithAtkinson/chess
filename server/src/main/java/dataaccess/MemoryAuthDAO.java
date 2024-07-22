package dataaccess;

import model.AuthData;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private final Map<String, AuthData> authDataMap = new HashMap<>();

    @Override
    public AuthData getAuthData(String authToken) {
        return authDataMap.get(authToken);
    }

    @Override
    public boolean addAuthData(AuthData authData) {
        if (authDataMap.containsKey(authData.getAuthToken())) {
            return false;
        }
        authDataMap.put(authData.getAuthToken(), authData);
        return true;
    }

    @Override
    public void clear() {
        authDataMap.clear();
    }

    @Override
    public List<AuthData> getAllAuthData() {
        return new ArrayList<>(authDataMap.values());
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






