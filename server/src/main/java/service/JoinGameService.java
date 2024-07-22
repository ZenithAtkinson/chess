package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import request.JoinGameRequest;

public class JoinGameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public JoinGameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public void joinGame(JoinGameRequest request, String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData != null) {
            GameData game = gameDAO.getGame(request.getGameID());
            if (game != null) {
                if (request.getPlayerColor().equals("WHITE")) {
                    game.setWhiteUsername(authData.getUsername());
                } else if (request.getPlayerColor().equals("BLACK")) {
                    game.setBlackUsername(authData.getUsername());
                } else {
                    throw new DataAccessException("Error: invalid player color");
                }
                gameDAO.updateGame(game);
            } else {
                throw new DataAccessException("Error: game not found");
            }
        } else {
            throw new DataAccessException("Error: unauthorized");
        }
    }
}
