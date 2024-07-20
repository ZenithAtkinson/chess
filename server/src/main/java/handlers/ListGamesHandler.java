package handlers;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.GameData;
import Service.ListGamesService;
import java.util.List;

public class ListGamesHandler extends HandlerForHttps<Void> {

    private final ListGamesService listGamesService;

    public ListGamesHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.listGamesService = new ListGamesService(gameDAO, authDAO);
    }

    @Override
    protected Class<Void> getRequestClass() {
        return Void.class;
    }

    @Override
    protected List<GameData> getResult(Void request, String authToken) throws Exception {
        return listGamesService.listGames(authToken);
    }
}
