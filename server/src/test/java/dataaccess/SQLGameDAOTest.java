package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SQLGameDAOTest {
    private static SQLGameDAO gameDAO;
    private static Connection conn;
    private static Gson gson;

    @BeforeAll
    public static void setup() throws SQLException, DataAccessException {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/chessdb", "username", "password");
        gameDAO = new SQLGameDAO(conn);
        gson = new Gson();
        gameDAO.clear();
    }

    @AfterAll
    public static void teardown() throws SQLException {
        conn.close();
    }

    @Test
    @Order(1)
    public void testAddGame() throws DataAccessException {
        GameData gameData = new GameData(1, "whiteUser", "blackUser", "Test Game", null);
        ChessGame game = new ChessGame();
        gameDAO.addGame(gameData, game);
        ChessGame retrievedGame = gameDAO.getGame(1);
        assertNotNull(retrievedGame);
    }

    @Test
    @Order(2)
    public void testGetGame() throws DataAccessException {
        ChessGame game = gameDAO.getGame(1);
        assertNotNull(game);
        assertEquals(ChessGame.TeamColor.WHITE, game.getTeamTurn());
    }

    @Test
    @Order(3)
    public void testGetGameNegative() {
        assertThrows(DataAccessException.class, () -> gameDAO.getGame(999));
    }

    @Test
    @Order(4)
    public void testUpdateGame() throws DataAccessException {
        GameData gameData = new GameData(1, "whiteUser", "blackUser", "Updated Game", null);
        ChessGame game = gameDAO.getGame(1);
        game.setTeamTurn(ChessGame.TeamColor.BLACK);
        gameDAO.updateGame(gameData, game);
        ChessGame updatedGame = gameDAO.getGame(1);
        assertEquals(ChessGame.TeamColor.BLACK, updatedGame.getTeamTurn());
    }

    @Test
    @Order(5)
    public void testGetAllGames() throws DataAccessException {
        GameData gameData2 = new GameData(2, "whiteUser2", "blackUser2", "Test Game 2", null);
        ChessGame game2 = new ChessGame();
        gameDAO.addGame(gameData2, game2);
        List<GameData> allGames = gameDAO.getAllGames();
        assertEquals(2, allGames.size());
    }

    @Test
    @Order(6)
    public void testClear() throws DataAccessException {
        gameDAO.clear();
        List<GameData> allGames = gameDAO.getAllGames();
        assertTrue(allGames.isEmpty());
    }

    @Test
    @Order(7)
    public void testAddGameState() throws DataAccessException {
        GameData gameData = new GameData(3, "whiteUser3", "blackUser3", "Test Game 3", null);
        ChessGame game = new ChessGame();
        gameDAO.addGame(gameData, game);
        ChessGame retrievedGame = gameDAO.getGame(3);
        assertNotNull(retrievedGame);
    }

    @Test
    @Order(8)
    public void testGetGameState() throws DataAccessException {
        ChessGame game = gameDAO.getGame(3);
        assertNotNull(game);
        assertEquals(ChessGame.TeamColor.WHITE, game.getTeamTurn());
    }

    @Test
    @Order(9)
    public void testUpdateGameState() throws DataAccessException {
        GameData gameData = new GameData(3, "whiteUser3", "blackUser3", "Updated Game 3", null);
        ChessGame game = gameDAO.getGame(3);
        game.setTeamTurn(ChessGame.TeamColor.BLACK);
        gameDAO.updateGame(gameData, game);
        ChessGame updatedGame = gameDAO.getGame(3);
        assertEquals(ChessGame.TeamColor.BLACK, updatedGame.getTeamTurn());
    }

    @Test
    @Order(10)
    public void testAddGameNegative() {
        GameData gameData = new GameData(1, "whiteUser", "blackUser", "Test Game", null);
        ChessGame game = new ChessGame();
        assertThrows(DataAccessException.class, () -> gameDAO.addGame(gameData, game));
    }
}
