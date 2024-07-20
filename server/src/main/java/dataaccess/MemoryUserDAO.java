package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private Map<String, UserData> users = new HashMap<>();

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public boolean addUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.getUsername())) {
            throw new DataAccessException("User already exists");
        }
        users.put(user.getUsername(), user);
        return true;
    }

    @Override
    public boolean updateUser(UserData user) throws DataAccessException {
        if (!users.containsKey(user.getUsername())) {
            throw new DataAccessException("User does not exist");
        }
        users.put(user.getUsername(), user);
        return true;
    }

    @Override
    public boolean deleteUser(String username) throws DataAccessException {
        if (!users.containsKey(username)) {
            throw new DataAccessException("User does not exist");
        }
        users.remove(username);
        return true;
    }
}
