package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import response.ListGamesResult;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class ListGamesService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ListGamesService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        //System.out.println("listGames - Auth Token Received: " + authToken);
        AuthData authData = authDAO.getAuthData(authToken);
        //System.out.println("listGames - Auth Data Retrieved: " + authData);
        if (authData != null) {
            Collection<GameData> gamesCollection = gameDAO.getAllGames();
            List<GameData> gamesList = new ArrayList<>(gamesCollection); //List<GameData> from Collection?
            //System.out.println("listGames - Games Retrieved: " + gamesList);
            return new ListGamesResult(gamesList);
        } else {
            throw new DataAccessException("Error: unauthorized");
        }
    }
}
