package handlers;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import request.CreateGameRequest;
import response.CreateGameResult;
import service.GameService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public CreateGameHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameService = new GameService(gameDAO, authDAO);
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        // Deserialize the request object from JSON
        CreateGameRequest createGameRequest = gson.fromJson(request.body(), CreateGameRequest.class);

        // Get the auth token from the header
        String authToken = request.headers("Authorization");

        // Process the request and get the result
        CreateGameResult result = gameService.createGame(createGameRequest, authToken);

        // Serialize the result object to JSON
        response.type("application/json");
        return gson.toJson(result);
    }
}
