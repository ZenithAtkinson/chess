package Service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public GameData createGame(GameData request, String authToken) throws Exception {
        try {
            AuthData authData = authorize(authToken);

            if (request.getGameName() == null) {
                throw new IllegalArgumentException("Game name cannot be null");
            }

            GameData newGame = new GameData(0, authData.getUsername(), null, request.getGameName(), new ChessGame());
            gameDAO.addGame(newGame);

            return newGame;
        } catch (DataAccessException e) {
            throw new Exception(e);
        }
    }

    private AuthData authorize(String authToken) throws Exception {
        try {
            AuthData authData = authDAO.getAuthData(authToken);
            if (authData == null) {
                throw new SecurityException("Error: Unauthorized");
            }
            return authData;
        } catch (DataAccessException e) {
            throw new Exception(e);
        }
    }
}
