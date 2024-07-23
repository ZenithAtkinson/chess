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
    //Handle the HTTP request to create a game
    public Object handle(Request req, Response res) throws Exception {
        //parse the create game request from the request body
        CreateGameRequest request = gson.fromJson(req.body(), CreateGameRequest.class);
        //Get the authorization token from the request headers
        String authToken = req.headers("Authorization");
        CreateGameResult response;

        try {
            response = createGameService.createGame(request, authToken);
            res.status(200);
        } catch (DataAccessException e) {
            res.status(401);// Status for unauthorized
            response = new CreateGameResult("Error: unauthorized");
        }

        return gson.toJson(response);
    }
}
