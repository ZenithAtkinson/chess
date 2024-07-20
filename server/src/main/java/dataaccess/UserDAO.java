package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;
    boolean addUser(UserData user) throws DataAccessException;
    boolean updateUser(UserData user) throws DataAccessException;
    boolean deleteUser(String username) throws DataAccessException;
}