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
        //Get user data
        return userDataMap.get(username);
    }

    @Override
    // Add a new user
    public boolean addUser(UserData user) {
        if (userDataMap.containsKey(user.getUsername())) {
            return false; // User already exists
        }
        userDataMap.put(user.getUsername(), user);
        return true;
    }


    @Override
    // Clear all users
    public void clear() {
        userDataMap.clear();
    }

    @Override
    //Get all users
    public List<UserData> getAllUsers() {
        return new ArrayList<>(userDataMap.values());
    }
}
