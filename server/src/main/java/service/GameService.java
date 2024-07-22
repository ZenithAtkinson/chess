package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import response.CreateGameResult;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        AuthData authData = authorize(authToken);
        if (request.getGameName() == null || request.getGameName().isEmpty()) {
            throw new DataAccessException("Error: Game name cannot be null or empty");
        }
        GameData newGame = new GameData(0, authData.getUsername(), null, request.getGameName(), null);
        gameDAO.addGame(newGame);
        return new CreateGameResult(newGame.getGameID());
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
                throw new DataAccessException("Error: invalid player color or player color already taken");
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
}
