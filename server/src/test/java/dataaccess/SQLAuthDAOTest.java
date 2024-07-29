package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SQLAuthDAOTest {

    private SQLAuthDAO authDAO;

    @BeforeAll
    public void setUpClass() throws Exception {
        DatabaseManager.initializeDatabase();
    }

    @BeforeEach
    public void setUp() throws Exception {
        authDAO = new SQLAuthDAO();
        authDAO.clear(); // Clear auth tokens table before each test
    }

    @Test
    public void testAddAuthDataPositive() throws Exception {
        AuthData authData = new AuthData("testToken", "testUser");
        assertTrue(authDAO.addAuthData(authData));

        AuthData retrievedAuthData = authDAO.getAuthData("testToken");
        assertNotNull(retrievedAuthData);
        assertEquals("testToken", retrievedAuthData.getAuthToken());
        assertEquals("testUser", retrievedAuthData.getUsername());
    }

    @Test
    public void testAddAuthDataNegative() throws Exception {
        AuthData authData1 = new AuthData("testToken", "testUser");
        AuthData authData2 = new AuthData("testToken", "otherUser");
        authDAO.addAuthData(authData1);

        assertThrows(DataAccessException.class, () -> {
            authDAO.addAuthData(authData2);
        });
    }

    @Test
    public void testGetAuthDataPositive() throws Exception {
        AuthData authData = new AuthData("testToken", "testUser");
        authDAO.addAuthData(authData);

        AuthData retrievedAuthData = authDAO.getAuthData("testToken");
        assertNotNull(retrievedAuthData);
        assertEquals("testToken", retrievedAuthData.getAuthToken());
        assertEquals("testUser", retrievedAuthData.getUsername());
    }

    @Test
    public void testGetAuthDataNegative() throws Exception {
        AuthData retrievedAuthData = authDAO.getAuthData("nonExistentToken");
        assertNull(retrievedAuthData);
    }

    @Test
    public void testDeleteAuthDataPositive() throws Exception {
        AuthData authData = new AuthData("testToken", "testUser");
        authDAO.addAuthData(authData);
        assertTrue(authDAO.deleteAuthData("testToken"));

        AuthData retrievedAuthData = authDAO.getAuthData("testToken");
        assertNull(retrievedAuthData);
    }

    @Test
    public void testDeleteAuthDataNegative() throws Exception {
        assertFalse(authDAO.deleteAuthData("nonExistentToken"));
    }

    @Test
    public void testClearPositive() throws Exception {
        AuthData authData = new AuthData("testToken", "testUser");
        authDAO.addAuthData(authData);
        authDAO.clear();

        AuthData retrievedAuthData = authDAO.getAuthData("testToken");
        assertNull(retrievedAuthData);
    }

    @Test
    public void testGetAllAuthDataPositive() throws Exception {
        AuthData authData1 = new AuthData("token1", "user1");
        AuthData authData2 = new AuthData("token2", "user2");
        authDAO.addAuthData(authData1);
        authDAO.addAuthData(authData2);

        List<AuthData> tokens = authDAO.getAllAuthData();
        assertEquals(2, tokens.size());

        AuthData retrievedAuthData1 = tokens.get(0);
        AuthData retrievedAuthData2 = tokens.get(1);

        assertEquals("token1", retrievedAuthData1.getAuthToken());
        assertEquals("user1", retrievedAuthData1.getUsername());

        assertEquals("token2", retrievedAuthData2.getAuthToken());
        assertEquals("user2", retrievedAuthData2.getUsername());
    }

    @Test
    public void testGenerateAuthTokenPositive() throws Exception {
        String authToken = authDAO.generateAuthToken("testUser");
        assertNotNull(authToken);

        AuthData retrievedAuthData = authDAO.getAuthData(authToken);
        assertNotNull(retrievedAuthData);
        assertEquals(authToken, retrievedAuthData.getAuthToken());
        assertEquals("testUser", retrievedAuthData.getUsername());
    }
}
