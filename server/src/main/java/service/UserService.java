package service;

import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

  //Unused write hashed password

    //Read the hashed password from the database
    public String readHashedPasswordFromDatabase(String username) throws DataAccessException {
        UserData user = userDAO.getUser(username);
        if (user != null) {
            return user.getPassword();
        } else {
            throw new DataAccessException("User not found");
        }
    }

    // Verify user login by comparing the password
    public boolean verifyUser(String username, String password) throws DataAccessException {
        String hashedPassword = readHashedPasswordFromDatabase(username);
        return BCrypt.checkpw(password, hashedPassword);
    }
}
