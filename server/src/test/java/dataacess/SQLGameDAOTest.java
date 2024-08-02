package dataacess;

import dataaccess.DataAccessException;
import dataaccess.SQLGameDAO;
import model.GameData;
import chess.ChessGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLGameDAOTest {
    private SQLGameDAO gameDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        gameDAO = new SQLGameDAO();
        gameDAO.clear();
    }

    @Test
    void testAddGamePositive() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "player1", "player2", "Test Game", chessGame, "none");
        assertTrue(gameDAO.addGame(game));
        assertTrue(game.getGameID() > 0); // Ensure gameID is set correctly
    }

    @Test
    void testAddGameNegative() throws DataAccessException {
        ChessGame chessGame1 = new ChessGame();
        ChessGame chessGame2 = new ChessGame();
        GameData game1 = new GameData(0, "player1", "player2", "Test Game", chessGame1, "none");
        gameDAO.addGame(game1);
        GameData game2 = new GameData(0, "player3", "player4", "Test Game", chessGame2, "none");
        assertThrows(DataAccessException.class, () -> gameDAO.addGame(game2));
    }

    @Test
    void testGetGamePositive() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "player1", "player2", "Test Game", chessGame, "none");
        gameDAO.addGame(game);
        GameData retrievedGame = gameDAO.getGame(game.getGameID());
        assertEquals(game, retrievedGame);
    }

    @Test
    void testGetGameNegative() throws DataAccessException {
        assertNull(gameDAO.getGame(999));
    }

    @Test
    void testUpdateGamePositive() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "player1", "player2", "Test Game", chessGame, "none");
        gameDAO.addGame(game);
        game.setGameName("Updated Game");
        assertTrue(gameDAO.updateGame(game));
        GameData updatedGame = gameDAO.getGame(game.getGameID());
        assertEquals("Updated Game", updatedGame.getGameName());
    }

    @Test
    void testUpdateGameNegative() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(999, "player1", "player2", "Non-existent Game", chessGame, "none");
        assertFalse(gameDAO.updateGame(game));
    }

    @Test
    void testDeleteGamePositive() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "player1", "player2", "Test Game", chessGame, "none");
        gameDAO.addGame(game);
        assertTrue(gameDAO.deleteGame(game.getGameID()));
        assertNull(gameDAO.getGame(game.getGameID()));
    }

    @Test
    void testDeleteGameNegative() throws DataAccessException {
        assertFalse(gameDAO.deleteGame(999));
    }

    @Test
    void testGetAllGamesPositive() throws DataAccessException {
        ChessGame chessGame1 = new ChessGame();
        ChessGame chessGame2 = new ChessGame();
        GameData game1 = new GameData(0, "player1", "player2", "Test Game 1", chessGame1, "none");
        GameData game2 = new GameData(0, "player3", "player4", "Test Game 2", chessGame2, "none");
        gameDAO.addGame(game1);
        gameDAO.addGame(game2);
        assertEquals(2, gameDAO.getAllGames().size());
    }

    @Test
    void testClear() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "player1", "player2", "Test Game", chessGame, "none");
        gameDAO.addGame(game);
        gameDAO.clear();
        assertNull(gameDAO.getGame(game.getGameID()));
    }
}
