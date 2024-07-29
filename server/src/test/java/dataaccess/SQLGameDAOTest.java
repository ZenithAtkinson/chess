package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTest {
    private static DatabaseManager dbManager;
    private GameDAO gameDAO;
    private Gson gson;

    @BeforeAll
    public static void setupClass() {
        dbManager = new DatabaseManager();
        dbManager.initialize(); // Ensure this method sets up the DB connection
    }

    @BeforeEach
    public void setup() {
        gameDAO = new SQLGameDAO();
        gson = new Gson();
        try {
            DatabaseManager.initializeDatabase(); // Use the static method from DatabaseManager to initialize the database
            gameDAO.clear();
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("Database initialization or clearing failed");
        }
    }

    @Test
    public void testSerialization() throws DataAccessException {
        ChessGame originalGame = new ChessGame();
        // You may set up other properties of the game as needed

        // Save the game to the database with a specific ID
        int gameId = 1; // Or generate it dynamically
        gameDAO.addGameState(gameId, originalGame);

        // Retrieve the game state from the database
        ChessGame retrievedGame = ((SQLGameDAO) gameDAO).getGameState(gameId);
        // Compare the JSON representations of the original and retrieved game states
        assertEquals(gson.toJson(originalGame), gson.toJson(retrievedGame), "The retrieved game state should match the original");
    }

    @Test
    public void testAddAndGetGame() throws DataAccessException {
        GameData gameData = new GameData(1, "whitePlayer", "blackPlayer", "Test Game", "Additional Params");
        gameDAO.addGame(gameData);

        GameData retrievedGame = gameDAO.getGame(1);
        assertEquals(gameData.getGameID(), retrievedGame.getGameID());
        assertEquals(gameData.getWhiteUsername(), retrievedGame.getWhiteUsername());
        assertEquals(gameData.getBlackUsername(), retrievedGame.getBlackUsername());
        assertEquals(gameData.getGameName(), retrievedGame.getGameName());
        assertEquals(gameData.getAdditionalParameter(), retrievedGame.getAdditionalParameter());
    }

    @Test
    public void testUpdateGame() throws DataAccessException {
        GameData gameData = new GameData(1, "whitePlayer", "blackPlayer", "Test Game", "Additional Params");
        gameDAO.addGame(gameData);

        gameData.setGameName("Updated Game");
        gameDAO.updateGame(gameData);

        GameData retrievedGame = gameDAO.getGame(1);
        assertEquals("Updated Game", retrievedGame.getGameName());
    }

    @Test
    public void testClear() throws DataAccessException {
        GameData gameData1 = new GameData(1, "whitePlayer", "blackPlayer", "Test Game 1", "Additional Params 1");
        GameData gameData2 = new GameData(2, "whitePlayer2", "blackPlayer2", "Test Game 2", "Additional Params 2");
        gameDAO.addGame(gameData1);
        gameDAO.addGame(gameData2);

        gameDAO.clear();

        Collection<GameData> allGames = gameDAO.getAllGames();
        assertTrue(allGames.isEmpty(), "Database should be empty after clear");
    }

    @Test
    public void testGetAllGames() throws DataAccessException {
        GameData gameData1 = new GameData(1, "whitePlayer", "blackPlayer", "Test Game 1", "Additional Params 1");
        GameData gameData2 = new GameData(2, "whitePlayer2", "blackPlayer2", "Test Game 2", "Additional Params 2");
        gameDAO.addGame(gameData1);
        gameDAO.addGame(gameData2);

        Collection<GameData> allGames = gameDAO.getAllGames();
        assertEquals(2, allGames.size());
    }
}
