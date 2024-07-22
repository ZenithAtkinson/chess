package handlers;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import response.ListGamesResult;
import service.ListGamesService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler implements Route {
    private final ListGamesService listGamesService;
    private final Gson gson = new Gson();

    public ListGamesHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.listGamesService = new ListGamesService(gameDAO, authDAO);
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        // Get the auth token from the header
        String authToken = request.headers("Authorization");

        // Process the request and get the result
        ListGamesResult result = listGamesService.listGames(authToken); // Ensure this matches the return type

        // Serialize the result object to JSON
        response.type("application/json");
        return gson.toJson(result);
    }
}
