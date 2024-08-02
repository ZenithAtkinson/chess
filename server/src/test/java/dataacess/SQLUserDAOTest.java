package dataacess;

import dataaccess.DataAccessException;
import dataaccess.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserDAOTest {
    private SQLUserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new SQLUserDAO();
        userDAO.clear();
    }

    @Test
    void testAddUserPositive() throws DataAccessException {
        UserData user = new UserData("user1", "password1", "email1");
        assertTrue(userDAO.addUser(user));
    }

    @Test
    void testAddUserNegative() throws DataAccessException {
        UserData user = new UserData("user1", "password1", "email1");
        userDAO.addUser(user);
        assertThrows(DataAccessException.class, () -> userDAO.addUser(user));
    }

    @Test
    void testGetUserPositive() throws DataAccessException {
        UserData user = new UserData("user1", "password1", "email1");
        userDAO.addUser(user);
        UserData retrievedUser = userDAO.getUser("user1");
        assertEquals(user, retrievedUser);
    }

    @Test
    void testGetUserNegative() throws DataAccessException {
        assertNull(userDAO.getUser("invalid_user"));
    }

    @Test
    void testUpdateUserPositive() throws DataAccessException {
        UserData user = new UserData("user1", "password1", "email1");
        userDAO.addUser(user);
        //user.setEmail("new_email1");
        assertTrue(userDAO.updateUser(user));
        UserData updatedUser = userDAO.getUser("user1");
        assertEquals("new_email1", updatedUser.getEmail());
    }

    @Test
    void testUpdateUserNegative() throws DataAccessException {
        UserData user = new UserData("invalid_user", "password1", "email1");
        assertFalse(userDAO.updateUser(user));
    }

    @Test
    void testDeleteUserPositive() throws DataAccessException {
        UserData user = new UserData("user1", "password1", "email1");
        userDAO.addUser(user);
        assertTrue(userDAO.deleteUser("user1"));
        assertNull(userDAO.getUser("user1"));
    }

    @Test
    void testDeleteUserNegative() throws DataAccessException {
        assertFalse(userDAO.deleteUser("invalid_user"));
    }

    @Test
    void testGetAllUsersPositive() throws DataAccessException {
        UserData user1 = new UserData("user1", "password1", "email1");
        UserData user2 = new UserData("user2", "password2", "email2");
        userDAO.addUser(user1);
        userDAO.addUser(user2);
        Collection<UserData> users = userDAO.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    void testClear() throws DataAccessException {
        UserData user = new UserData("user1", "password1", "email1");
        userDAO.addUser(user);
        userDAO.clear();
        assertNull(userDAO.getUser("user1"));
    }
}
