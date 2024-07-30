package service;

import dataaccess.SQLUserDAO;
import dataaccess.UserDAO;
import model.UserData;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService {
    private final UserDAO userDAO;
    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    public UserService() {
        this.userDAO = new SQLUserDAO();
    }

    public boolean register(UserData user) {
        logger.log(Level.INFO, "Registering user: {0}", user.getUsername());
        try {
            return userDAO.addUser(user);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error registering user: " + e.getMessage(), e);
            return false;
        }
    }

    public UserData getUser(String username) {
        logger.log(Level.INFO, "Retrieving user: {0}", username);
        try {
            return userDAO.getUser(username);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving user: " + e.getMessage(), e);
            return null;
        }
    }

    public void clear() {
        logger.log(Level.INFO, "Clearing users");
        try {
            userDAO.clear();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error clearing users: " + e.getMessage(), e);
        }
    }
}
