package Service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import response.ListGamesResult;

import java.util.List;

public class ListGamesService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ListGamesService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData != null) {
            List<GameData> games = gameDAO.getAllGames();
            return new ListGamesResult(games); // Return ListGamesResult instead of List<GameData>
        } else {
            throw new DataAccessException("Error: unauthorized");
        }
    }
}
