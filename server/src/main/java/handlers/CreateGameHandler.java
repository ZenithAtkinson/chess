package handlers;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import request.CreateGameRequest;
import response.CreateGameResult;
import service.CreateGameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    private final CreateGameService createGameService;
    private final Gson gson = new Gson();

    public CreateGameHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.createGameService = new CreateGameService(gameDAO, authDAO);
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        System.out.println("CreateGameHandler: Received create game request.");
        CreateGameRequest request = gson.fromJson(req.body(), CreateGameRequest.class);
        String authToken = req.headers("Authorization");

        if (authToken != null && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring("Bearer ".length());
        }

        CreateGameResult response;
        try {
            response = createGameService.createGame(request, authToken);
            res.status(200);
        } catch (DataAccessException e) {
            res.status(401);
            response = new CreateGameResult("Error: unauthorized");
        }

        return gson.toJson(response);
    }
}
