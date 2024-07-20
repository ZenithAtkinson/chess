package handlers;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import Service.GameService;

import java.util.Map;

public class JoinGameHandler extends HandlerForHttps<Object> {

    private final GameService gameService;

    public JoinGameHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameService = new GameService(gameDAO, authDAO);
    }

    @Override
    protected Class<Object> getRequestClass() {
        return Object.class;
    }

    @Override
    protected Object getResult(Object request, String authToken) throws Exception {
        GameService.JoinGameRequest joinGameRequest = new GameService.JoinGameRequest(
                ((Number) ((Map<?, ?>) request).get("gameID")).intValue(),
                (String) ((Map<?, ?>) request).get("playerColor")
        );
        gameService.joinGame(joinGameRequest, authToken);
        return null;
    }
}
