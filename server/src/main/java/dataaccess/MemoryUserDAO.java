package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    // Get user by username
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    // Add a new user
    public boolean addUser(UserData user) {
        if (users.containsKey(user.getUsername())) {
            return false;
        }
        users.put(user.getUsername(), user);
        return true;
    }

    @Override
    // Update existing user
    public boolean updateUser(UserData user) {
        if (!users.containsKey(user.getUsername())) {
            return false;
        }
        users.put(user.getUsername(), user);
        return true;
    }

    @Override
    // Delete a user by username
    public boolean deleteUser(String username) {
        if (!users.containsKey(username)) {
            return false;
        }
        users.remove(username);
        return true;
    }

    @Override
    // Clear all users from memory
    public void clear() {
        users.clear();
    }

    @Override
    // Get all users
    public List<UserData> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
