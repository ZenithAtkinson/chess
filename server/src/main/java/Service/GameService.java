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

    public void joinGame(JoinGameRequest request, String authToken) throws DataAccessException {
        AuthData authData = authorize(authToken);
        GameData game = gameDAO.getGame(request.getGameID());
        if (game != null) {
            if ("WHITE".equals(request.getPlayerColor()) && game.getWhiteUsername() == null) {
                game.setWhiteUsername(authData.getUsername());
            } else if ("BLACK".equals(request.getPlayerColor()) && game.getBlackUsername() == null) {
                game.setBlackUsername(authData.getUsername());
            } else {
                throw new DataAccessException("Error: player color already taken or invalid color");
            }
            gameDAO.updateGame(game);
        } else {
            throw new DataAccessException("Error: game not found");
        }
    }

    private AuthData authorize(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        return authData;
    }

    public static class JoinGameRequest {
        private final int gameID;
        private final String playerColor;

        public JoinGameRequest(int gameID, String playerColor) {
            this.gameID = gameID;
            this.playerColor = playerColor;
        }

        public int getGameID() {
            return gameID;
        }

        public String getPlayerColor() {
            return playerColor;
        }
    }
}
