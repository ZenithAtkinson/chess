package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SQLUserDAOTest {

    private SQLUserDAO userDAO;

    @BeforeAll
    public void setUpClass() throws Exception {
        DatabaseManager.initializeDatabase();
    }

    @BeforeEach
    public void setUp() throws Exception {
        userDAO = new SQLUserDAO();
        userDAO.clear(); // Clear users table before each test
    }

    @Test
    public void testAddUserPositive() throws Exception {
        UserData user = new UserData("testUser", "testPass", "test@example.com");
        assertTrue(userDAO.addUser(user));

        UserData retrievedUser = userDAO.getUser("testUser");
        assertNotNull(retrievedUser);
        assertEquals("testUser", retrievedUser.getUsername());
        assertTrue(BCrypt.checkpw("testPass", retrievedUser.getPassword())); // Verify hashed password
        assertEquals("test@example.com", retrievedUser.getEmail());
    }

    @Test
    public void testAddUserNegative() throws Exception {
        UserData user1 = new UserData("testUser", "testPass", "test@example.com");
        UserData user2 = new UserData("testUser", "otherPass", "other@example.com");
        userDAO.addUser(user1);

        assertThrows(DataAccessException.class, () -> {
            userDAO.addUser(user2);
        });
    }

    @Test
    public void testGetUserPositive() throws Exception {
        UserData user = new UserData("testUser", "testPass", "test@example.com");
        userDAO.addUser(user);

        UserData retrievedUser = userDAO.getUser("testUser");
        assertNotNull(retrievedUser);
        assertEquals("testUser", retrievedUser.getUsername());
        assertTrue(BCrypt.checkpw("testPass", retrievedUser.getPassword())); // Verify hashed password
        assertEquals("test@example.com", retrievedUser.getEmail());
    }

    @Test
    public void testGetUserNegative() throws Exception {
        UserData retrievedUser = userDAO.getUser("nonExistentUser");
        assertNull(retrievedUser);
    }

    @Test
    public void testClearPositive() throws Exception {
        UserData user = new UserData("testUser", "testPass", "test@example.com");
        userDAO.addUser(user);
        userDAO.clear();

        UserData retrievedUser = userDAO.getUser("testUser");
        assertNull(retrievedUser);
    }

    @Test
    public void testGetAllUsersPositive() throws Exception {
        UserData user1 = new UserData("user1", "pass1", "user1@example.com");
        UserData user2 = new UserData("user2", "pass2", "user2@example.com");
        userDAO.addUser(user1);
        userDAO.addUser(user2);

        List<UserData> users = userDAO.getAllUsers();
        assertEquals(2, users.size());

        UserData retrievedUser1 = users.get(0);
        UserData retrievedUser2 = users.get(1);

        assertEquals("user1", retrievedUser1.getUsername());
        assertTrue(BCrypt.checkpw("pass1", retrievedUser1.getPassword())); // Verify hashed password
        assertEquals("user1@example.com", retrievedUser1.getEmail());

        assertEquals("user2", retrievedUser2.getUsername());
        assertTrue(BCrypt.checkpw("pass2", retrievedUser2.getPassword())); // Verify hashed password
        assertEquals("user2@example.com", retrievedUser2.getEmail());
    }
}
