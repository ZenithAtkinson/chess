package handlers;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import request.JoinGameRequest;
import service.GameService;

public class JoinGameHandler extends HandlerForHttps<JoinGameRequest> {
    private final GameService gameService;

    public JoinGameHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameService = new GameService(gameDAO, authDAO);
    }

    @Override
    protected Class<JoinGameRequest> getRequestClass() {
        return JoinGameRequest.class;
    }

    @Override
    protected Object getResult(JoinGameRequest request, String authToken) throws Exception {
        gameService.joinGame(request, authToken);
        return null;
    }
}
