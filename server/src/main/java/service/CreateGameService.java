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

    // Create new game
    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        //Get authentication data using the auth token
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData == null) {
            throw new DataAccessException("Unauthorized"); // Throw exception if unauthorized
        }

        //Create a new game data object
        GameData game = new GameData(0, null, null, request.getGameName(), null);
        //add the game to the data store
        gameDAO.addGame(game);

        //Return the result of game creation
        return new CreateGameResult(game.getGameID());
    }
}
