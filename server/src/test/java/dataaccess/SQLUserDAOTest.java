package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;

import java.util.List;

public class SQLUserDAOTest {
    private SQLUserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new SQLUserDAO();
        DatabaseManager.initializeDatabase();
        userDAO.clear();
    }

    @Test
    public void testAddUser() throws DataAccessException {
        UserData user = new UserData("username", "password", "email@example.com");
        Assertions.assertTrue(userDAO.addUser(user));
    }

    @Test
    public void testAddUserNegative() throws DataAccessException {
        UserData user = new UserData("username", "password", "email@example.com");
        userDAO.addUser(user);
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.addUser(user));
    }

    @Test
    public void testGetUser() throws DataAccessException {
        UserData user = new UserData("username", "password", "email@example.com");
        userDAO.addUser(user);
        UserData retrieved = userDAO.getUser("username");
        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals("username", retrieved.getUsername());
    }

    @Test
    public void testGetUserNegative() throws DataAccessException {
        UserData retrieved = userDAO.getUser("username");
        Assertions.assertNull(retrieved);
    }

    @Test
    public void testClear() throws DataAccessException {
        UserData user = new UserData("username", "password", "email@example.com");
        userDAO.addUser(user);
        userDAO.clear();
        UserData retrieved = userDAO.getUser("username");
        Assertions.assertNull(retrieved);
    }

    @Test
    public void testGetAllUsers() throws DataAccessException {
        UserData user1 = new UserData("username1", "password1", "email1@example.com");
        UserData user2 = new UserData("username2", "password2", "email2@example.com");
        userDAO.addUser(user1);
        userDAO.addUser(user2);
        List<UserData> users = userDAO.getAllUsers();
        Assertions.assertEquals(2, users.size());
    }
}
