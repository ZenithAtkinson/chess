package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> userDataMap = new HashMap<>();

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return userDataMap.get(username);
    }

    @Override
    public boolean addUser(UserData user) throws DataAccessException {
        if (userDataMap.containsKey(user.getUsername())) {
            return false;
        }
        userDataMap.put(user.getUsername(), user);
        return true;
    }

    @Override
    public boolean updateUser(UserData user) throws DataAccessException {
        if (!userDataMap.containsKey(user.getUsername())) {
            return false;
        }
        userDataMap.put(user.getUsername(), user);
        return true;
    }

    @Override
    public boolean deleteUser(String username) throws DataAccessException {
        if (!userDataMap.containsKey(username)) {
            return false;
        }
        userDataMap.remove(username);
        return true;
    }

    @Override
    public void clear() throws DataAccessException {
        userDataMap.clear();
    }

    @Override
    public boolean usernameExists(String username) throws DataAccessException {
        return userDataMap.containsKey(username);
    }

    @Override
    public boolean verifyUser(UserData user) throws DataAccessException {
        UserData storedUser = userDataMap.get(user.getUsername());
        return storedUser != null && storedUser.getPassword().equals(user.getPassword());
    }

    @Override
    public List<UserData> getAllUsers() throws DataAccessException {
        return new ArrayList<>(userDataMap.values());
    }
}
