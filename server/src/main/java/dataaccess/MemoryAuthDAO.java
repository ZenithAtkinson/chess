package dataaccess;

import model.AuthData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//SEE scheme diagram for more
public class MemoryAuthDAO implements AuthDAO {
    private final Map<String, AuthData> authDataMap = new HashMap<>();

    @Override
    //Get authentication data by auth token
    public AuthData getAuthData(String authToken) {
        return authDataMap.get(authToken);
    }

    @Override
    //Add authentication data to datamap
    public boolean addAuthData(AuthData authData) {
        if (authDataMap.containsKey(authData.getAuthToken())) {
            return false;
        }
        authDataMap.put(authData.getAuthToken(), authData);
        return true;
    }

    @Override
    // Clear authentification
    public void clear() {
        authDataMap.clear();
    }

    @Override
    // Get auth data
    public List<AuthData> getAllAuthData() {
        return new ArrayList<>(authDataMap.values());
    }

    @Override
    // Delete authentication data by auth token
    public boolean deleteAuthData(String authToken) throws DataAccessException {
        if (!authDataMap.containsKey(authToken)) {
            throw new DataAccessException("Auth token does not exist");
        }
        authDataMap.remove(authToken);
        return true;
    }

    @Override
    // Generate new auth token
    public String generateAuthToken(String username) {
        String authToken = java.util.UUID.randomUUID().toString();
        authDataMap.put(authToken, new AuthData(authToken, username));
        return authToken;
    }
}
