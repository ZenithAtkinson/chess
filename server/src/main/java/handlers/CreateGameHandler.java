package handlers;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.GameData;
import Service.GameService;

public class CreateGameHandler extends HandlerForHttps<GameData> {

    private final GameService gameService;

    public CreateGameHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameService = new GameService(gameDAO, authDAO);
    }

    @Override
    protected Class<GameData> getRequestClass() {
        return GameData.class;
    }

    @Override
    protected GameData getResult(GameData request, String authToken) throws Exception {
        return gameService.createGame(request, authToken);
    }
}
