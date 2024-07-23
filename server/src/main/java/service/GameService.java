package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import response.CreateGameResult;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData != null) {
            if (request.getGameName() == null || request.getGameName().isEmpty()) {
                throw new DataAccessException("Error: Game name cannot be null or empty");
            }

            GameData newGame = new GameData(0, authData.getUsername(), null, request.getGameName(), null);
            gameDAO.addGame(newGame);

            return new CreateGameResult(newGame.getGameID());
        } else {
            throw new DataAccessException("Error: unauthorized");
        }
    }
}
