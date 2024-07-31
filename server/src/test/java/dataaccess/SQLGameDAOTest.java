package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SQLGameDAOTest {
    private static SQLGameDAO gameDAO;
    private static Connection conn;
    private static Gson gson;
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Unable to process db.properties. " + ex.getMessage());
        }
    }

    @BeforeAll
    public static void setup() throws SQLException, DataAccessException {
        conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
        conn.setCatalog(DATABASE_NAME);
        gameDAO = new SQLGameDAO(conn);
        gson = new Gson();
        gameDAO.clear();
    }

    @BeforeEach
    public void clearDatabase() throws DataAccessException {
        gameDAO.clear();
    }

    @AfterAll
    public static void teardown() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    @Test
    @Order(1)
    public void testAddGame() throws DataAccessException {
        addUser("whiteUser", "password", "whiteUser@example.com");
        addUser("blackUser", "password", "blackUser@example.com");

        GameData gameData = new GameData(1, "whiteUser", "blackUser", "Test Game", null);
        ChessGame game = new ChessGame();
        gameDAO.addGame(gameData, game);
        ChessGame retrievedGame = gameDAO.getGame(1);
        assertNotNull(retrievedGame);
    }

    @Test
    @Order(2)
    public void testGetGame() throws DataAccessException {
        System.out.println("Adding game with gameID 1");
        addUser("whiteUser", "password", "whiteUser@example.com");
        addUser("blackUser", "password", "blackUser@example.com");

        GameData gameData = new GameData(1, "whiteUser", "blackUser", "Test Game", null);
        ChessGame game = new ChessGame();
        gameDAO.addGame(gameData, game);

        System.out.println("Retrieving game with gameID 1");
        ChessGame retrievedGame = gameDAO.getGame(1);
        assertNotNull(retrievedGame);
        assertEquals(ChessGame.TeamColor.WHITE, retrievedGame.getTeamTurn());
    }

    @Test
    @Order(3)
    public void testGetGameNegative() {
        assertThrows(DataAccessException.class, () -> gameDAO.getGame(999));
    }

    @Test
    @Order(4)
    public void testUpdateGame() throws DataAccessException {
        System.out.println("Adding game with gameID 1");
        addUser("whiteUser", "password", "whiteUser@example.com");
        addUser("blackUser", "password", "blackUser@example.com");

        GameData gameData = new GameData(1, "whiteUser", "blackUser", "Test Game", null);
        ChessGame game = new ChessGame();
        gameDAO.addGame(gameData, game);

        System.out.println("Updating game with gameID 1");
        gameData.setGameName("Updated Game");
        game.setTeamTurn(ChessGame.TeamColor.BLACK);
        gameDAO.updateGame(gameData, game);

        System.out.println("Retrieving updated game with gameID 1");
        ChessGame updatedGame = gameDAO.getGame(1);
        assertNotNull(updatedGame);
        assertEquals(ChessGame.TeamColor.BLACK, updatedGame.getTeamTurn());
    }

    @Test
    @Order(5)
    public void testGetAllGames() throws DataAccessException {
        addUser("whiteUser2", "password", "whiteUser2@example.com");
        addUser("blackUser2", "password", "blackUser2@example.com");

        GameData gameData2 = new GameData(2, "whiteUser2", "blackUser2", "Test Game 2", null);
        ChessGame game2 = new ChessGame();
        gameDAO.addGame(gameData2, game2);

        List<GameData> allGames = gameDAO.getAllGames();
        assertEquals(1, allGames.size());
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
        addUser("whiteUser3", "password", "whiteUser3@example.com");
        addUser("blackUser3", "password", "blackUser3@example.com");

        GameData gameData = new GameData(3, "whiteUser3", "blackUser3", "Test Game 3", null);
        ChessGame game = new ChessGame();
        gameDAO.addGame(gameData, game);
        ChessGame retrievedGame = gameDAO.getGame(3);
        assertNotNull(retrievedGame);
    }

    @Test
    @Order(8)
    public void testGetGameState() throws DataAccessException {
        System.out.println("Adding game with gameID 3");
        addUser("whiteUser3", "password", "whiteUser3@example.com");
        addUser("blackUser3", "password", "blackUser3@example.com");

        GameData gameData = new GameData(3, "whiteUser3", "blackUser3", "Test Game 3", null);
        ChessGame game = new ChessGame();
        gameDAO.addGame(gameData, game);

        System.out.println("Retrieving game with gameID 3");
        ChessGame retrievedGame = gameDAO.getGame(3);
        assertNotNull(retrievedGame);
        assertEquals(ChessGame.TeamColor.WHITE, retrievedGame.getTeamTurn());
    }

    @Test
    @Order(9)
    public void testUpdateGameState() throws DataAccessException {
        System.out.println("Adding game with gameID 3");
        addUser("whiteUser3", "password", "whiteUser3@example.com");
        addUser("blackUser3", "password", "blackUser3@example.com");

        GameData gameData = new GameData(3, "whiteUser3", "blackUser3", "Test Game 3", null);
        ChessGame game = new ChessGame();
        gameDAO.addGame(gameData, game);

        System.out.println("Retrieving game with gameID 3 before update");
        ChessGame gameBeforeUpdate = gameDAO.getGame(3);
        assertNotNull(gameBeforeUpdate);

        System.out.println("Updating game with gameID 3");
        gameData.setGameName("Updated Game 3");
        gameBeforeUpdate.setTeamTurn(ChessGame.TeamColor.BLACK);
        gameDAO.updateGame(gameData, gameBeforeUpdate);

        System.out.println("Retrieving updated game with gameID 3");
        ChessGame updatedGame = gameDAO.getGame(3);
        assertNotNull(updatedGame);
        assertEquals(ChessGame.TeamColor.BLACK, updatedGame.getTeamTurn());
    }

    @Test
    @Order(10)
    public void testAddGameNegative() {
        GameData gameData = new GameData(1, "whiteUser", "blackUser", "Test Game", null);
        ChessGame game = new ChessGame();
        assertThrows(DataAccessException.class, () -> gameDAO.addGame(gameData, game));
    }

    private void addUser(String username, String password, String email) throws DataAccessException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
            conn.setCatalog(DATABASE_NAME);
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)")) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, email);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error adding users", e);
        }
    }
}
