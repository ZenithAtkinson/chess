package dataaccess;

import model.UserData;
import java.util.List;

public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;
    boolean addUser(UserData user) throws DataAccessException;
    boolean updateUser(UserData user) throws DataAccessException;
    boolean deleteUser(String username) throws DataAccessException;
    void clear() throws DataAccessException;
    List<UserData> getAllUsers() throws DataAccessException;
}
