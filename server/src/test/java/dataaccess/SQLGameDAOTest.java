package dataaccess;

import model.GameData;
import org.junit.jupiter.api.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SQLGameDAOTest {

    private SQLGameDAO gameDAO;

    @BeforeAll
    public void setUpClass() throws Exception {
        DatabaseManager.initializeDatabase(); // Ensure the database and tables are created
    }

    @BeforeEach
    public void setUp() throws Exception {
        gameDAO = new SQLGameDAO();
        gameDAO.clear(); // Clear games table before each test
    }

    @Test
    public void testAddGamePositive() throws Exception {
        GameData game = new GameData(1, "whiteUser", "blackUser", "testGame", null);
        assertTrue(gameDAO.addGame(game));

        GameData retrievedGame = gameDAO.getGame(1);
        assertNotNull(retrievedGame);
        assertEquals(1, retrievedGame.getGameID());
        assertEquals("whiteUser", retrievedGame.getWhiteUsername());
        assertEquals("blackUser", retrievedGame.getBlackUsername());
        assertEquals("testGame", retrievedGame.getGameName());
    }

    @Test
    public void testAddGameNegative() throws Exception {
        GameData game1 = new GameData(1, "whiteUser", "blackUser", "testGame", null);
        GameData game2 = new GameData(1, "otherWhiteUser", "otherBlackUser", "otherGame", null);
        gameDAO.addGame(game1);

        assertThrows(DataAccessException.class, () -> {
            gameDAO.addGame(game2);
        });
    }

    @Test
    public void testGetGamePositive() throws Exception {
        GameData game = new GameData(1, "whiteUser", "blackUser", "testGame", null);
        gameDAO.addGame(game);

        GameData retrievedGame = gameDAO.getGame(1);
        assertNotNull(retrievedGame);
        assertEquals(1, retrievedGame.getGameID());
        assertEquals("whiteUser", retrievedGame.getWhiteUsername());
        assertEquals("blackUser", retrievedGame.getBlackUsername());
        assertEquals("testGame", retrievedGame.getGameName());
    }

    @Test
    public void testGetGameNegative() throws Exception {
        GameData retrievedGame = gameDAO.getGame(999);
        assertNull(retrievedGame);
    }

    @Test
    public void testUpdateGamePositive() throws Exception {
        GameData game = new GameData(1, "whiteUser", "blackUser", "testGame", null);
        gameDAO.addGame(game);

        game.setWhiteUsername("updatedWhiteUser");
        game.setBlackUsername("updatedBlackUser");
        game.setGameName("updatedGame");
        assertTrue(gameDAO.updateGame(game));

        GameData retrievedGame = gameDAO.getGame(1);
        assertNotNull(retrievedGame);
        assertEquals("updatedWhiteUser", retrievedGame.getWhiteUsername());
        assertEquals("updatedBlackUser", retrievedGame.getBlackUsername());
        assertEquals("updatedGame", retrievedGame.getGameName());
    }

    @Test
    public void testUpdateGameNegative() throws Exception {
        GameData game = new GameData(999, "whiteUser", "blackUser", "testGame", null);

        assertThrows(DataAccessException.class, () -> {
            gameDAO.updateGame(game);
        });
    }

    @Test
    public void testClearPositive() throws Exception {
        GameData game = new GameData(1, "whiteUser", "blackUser", "testGame", null);
        gameDAO.addGame(game);
        gameDAO.clear();

        GameData retrievedGame = gameDAO.getGame(1);
        assertNull(retrievedGame);
    }

    @Test
    public void testGetAllGamesPositive() throws Exception {
        GameData game1 = new GameData(1, "whiteUser1", "blackUser1", "testGame1", null);
        GameData game2 = new GameData(2, "whiteUser2", "blackUser2", "testGame2", null);
        gameDAO.addGame(game1);
        gameDAO.addGame(game2);

        Collection<GameData> games = gameDAO.getAllGames();
        assertEquals(2, games.size());

        GameData[] gameArray = games.toArray(new GameData[0]);
        assertEquals(1, gameArray[0].getGameID());
        assertEquals("whiteUser1", gameArray[0].getWhiteUsername());
        assertEquals("blackUser1", gameArray[0].getBlackUsername());
        assertEquals("testGame1", gameArray[0].getGameName());

        assertEquals(2, gameArray[1].getGameID());
        assertEquals("whiteUser2", gameArray[1].getWhiteUsername());
        assertEquals("blackUser2", gameArray[1].getBlackUsername());
        assertEquals("testGame2", gameArray[1].getGameName());
    }
}
