package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import request.JoinGameRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import chess.ChessGame;

public class JoinGameServiceTest {
    private JoinGameService joinGameService;
    private static GameDAO gameDAO;
    private static AuthDAO authDAO;

    @BeforeAll
    public static void setUpAll() {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        joinGameService = new JoinGameService(gameDAO, authDAO);
    }

    @Test
    public void joinGamePass() throws Exception {
        // Add game to join
        ChessGame chessGame = new ChessGame(); // Create a ChessGame object
        GameData gameData = new GameData(1, null, null, "testGame", chessGame, "none");
        gameDAO.addGame(gameData);

        // Add auth token
        AuthData authData = new AuthData("authToken", "testUser");
        authDAO.addAuthData(authData);

        JoinGameRequest request = new JoinGameRequest(1, "WHITE");
        joinGameService.joinGame(request, "authToken");

        // Verify updated
        GameData updatedGame = gameDAO.getGame(1);
        Assertions.assertEquals("testUser", updatedGame.getWhiteUsername());
    }

    @Test
    public void joinGameFail() {
        JoinGameRequest request = new JoinGameRequest(-1, "WHITE"); // Invalid game ID
        Assertions.assertThrows(DataAccessException.class, () -> {
            joinGameService.joinGame(request, "authToken");
        });
    }
}
