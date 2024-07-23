package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> userDataMap = new HashMap<>();

    @Override
    public UserData getUser(String username) {
        return userDataMap.get(username);
    }

    @Override
    public boolean addUser(UserData user) {
        if (userDataMap.containsKey(user.getUsername())) {
            return false; // User already exists
        }
        userDataMap.put(user.getUsername(), user);
        return true;
    }

    @Override
    public boolean updateUser(UserData user) {
        if (!userDataMap.containsKey(user.getUsername())) {
            return false;
        }
        userDataMap.put(user.getUsername(), user);
        return true;
    }

    @Override
    public boolean deleteUser(String username) {
        if (!userDataMap.containsKey(username)) {
            return false;
        }
        userDataMap.remove(username);
        return true;
    }

    @Override
    public void clear() {
        userDataMap.clear();
    }

    @Override
    public List<UserData> getAllUsers() {
        return new ArrayList<>(userDataMap.values());
    }
}
