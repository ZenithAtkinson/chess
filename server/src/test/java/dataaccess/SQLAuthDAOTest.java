package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.List;

public class SQLAuthDAOTest {
    private SQLAuthDAO authDAO;
    private SQLUserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new SQLAuthDAO();
        userDAO = new SQLUserDAO();
        DatabaseManager.initializeDatabase();
        authDAO.clear();
        userDAO.clear();

        // Create a user that can be referenced in the tests
        UserData user = new UserData("user1", "password", "email@example.com");
        userDAO.addUser(user);
    }

    @Test
    public void testAddAuthData() throws DataAccessException {
        AuthData authData = new AuthData("token1", "user1");
        Assertions.assertTrue(authDAO.addAuthData(authData));
    }

    @Test
    public void testAddAuthDataNegative() throws DataAccessException {
        AuthData authData = new AuthData("token1", "user1");
        authDAO.addAuthData(authData);
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.addAuthData(authData));
    }

    @Test
    public void testGetAuthData() throws DataAccessException {
        AuthData authData = new AuthData("token1", "user1");
        authDAO.addAuthData(authData);
        AuthData retrieved = authDAO.getAuthData("token1");
        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals("user1", retrieved.getUsername());
    }

    @Test
    public void testGetAuthDataNegative() throws DataAccessException {
        AuthData retrieved = authDAO.getAuthData("token1");
        Assertions.assertNull(retrieved);
    }

    @Test
    public void testDeleteAuthData() throws DataAccessException {
        AuthData authData = new AuthData("token1", "user1");
        authDAO.addAuthData(authData);
        Assertions.assertTrue(authDAO.deleteAuthData("token1"));
    }

    @Test
    public void testGetAllAuthData() throws DataAccessException {
        AuthData authData1 = new AuthData("token1", "user1");
        AuthData authData2 = new AuthData("token2", "user1");
        authDAO.addAuthData(authData1);
        authDAO.addAuthData(authData2);
        List<AuthData> authDataList = authDAO.getAllAuthData();
        Assertions.assertEquals(2, authDataList.size());
    }
}
