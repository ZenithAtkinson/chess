package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import response.CreateGameResult;

public class CreateGameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public CreateGameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData == null) {
            throw new DataAccessException("Unauthorized");
        }

        GameData game = new GameData(0, null, null, request.getGameName(), null);
        gameDAO.addGame(game);

        return new CreateGameResult(game.getGameID());
    }
}
