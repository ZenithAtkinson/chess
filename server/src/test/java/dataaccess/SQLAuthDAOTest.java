package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {
    private SQLAuthDAO authDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        authDAO = new SQLAuthDAO();
        authDAO.clear();
    }

    @Test
    void testAddAuthDataPositive() throws DataAccessException {
        AuthData authData = new AuthData("token1", "user1");
        assertTrue(authDAO.addAuthData(authData));
        assertEquals(authData, authDAO.getAuthData("token1"));
    }

    @Test
    void testAddAuthDataNegative() throws DataAccessException {
        AuthData authData1 = new AuthData("token1", "user1");
        authDAO.addAuthData(authData1);
        AuthData authData2 = new AuthData("token1", "user2");

        assertTrue(authDAO.addAuthData(authData2));
        AuthData retrievedAuthData = authDAO.getAuthData(authData2.getAuthToken());
        assertNotNull(retrievedAuthData);
        assertEquals("user2", retrievedAuthData.getUsername());
        assertNotEquals("token1", retrievedAuthData.getAuthToken());
    }

    @Test
    void testGetAuthDataPositive() throws DataAccessException {
        AuthData authData = new AuthData("token1", "user1");
        authDAO.addAuthData(authData);
        assertEquals(authData, authDAO.getAuthData("token1"));
    }

    @Test
    void testGetAuthDataNegative() throws DataAccessException {
        assertNull(authDAO.getAuthData("invalid_token"));
    }

    @Test
    void testDeleteAuthDataPositive() throws DataAccessException {
        AuthData authData = new AuthData("token1", "user1");
        authDAO.addAuthData(authData);
        assertTrue(authDAO.deleteAuthData("token1"));
        assertNull(authDAO.getAuthData("token1"));
    }

    @Test
    void testDeleteAuthDataNegative() {
        assertThrows(DataAccessException.class, () -> authDAO.deleteAuthData("invalid_token"));
    }

    @Test
    void testGetAllAuthDataPositive() throws DataAccessException {
        AuthData authData1 = new AuthData("token1", "user1");
        AuthData authData2 = new AuthData("token2", "user2");
        authDAO.addAuthData(authData1);
        authDAO.addAuthData(authData2);
        Collection<AuthData> authDataList = authDAO.getAllAuthData();
        assertEquals(2, authDataList.size());
    }

    @Test
    void testClear() throws DataAccessException {
        AuthData authData = new AuthData("token1", "user1");
        authDAO.addAuthData(authData);
        authDAO.clear();
        assertNull(authDAO.getAuthData("token1"));
    }
}
