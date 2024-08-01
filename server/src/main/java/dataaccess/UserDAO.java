package dataaccess;

import model.UserData;

import java.util.Collection;
import java.util.List;

public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;
    boolean addUser(UserData user) throws DataAccessException;
    boolean updateUser(UserData user) throws DataAccessException; // Added method
    boolean deleteUser(String username) throws DataAccessException; // Added method
    void clear() throws DataAccessException;
    Collection<UserData> getAllUsers() throws DataAccessException;
}
