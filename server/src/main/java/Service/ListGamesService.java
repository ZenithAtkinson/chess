package Service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

import java.util.List;

public class ListGamesService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ListGamesService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData != null) {
            return gameDAO.getAllGames();
        } else {
            throw new DataAccessException("Error: unauthorized");
        }
    }
}
