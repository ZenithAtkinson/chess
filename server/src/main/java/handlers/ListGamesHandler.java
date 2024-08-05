package handlers;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import response.ListGamesResult;
import service.ListGamesService;
import spark.Request;
import spark.Response;
import spark.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListGamesHandler implements Route {
    private final ListGamesService listGamesService;
    private final Gson gson = new Gson();
    private static final Logger LOGGER = LoggerFactory.getLogger(ListGamesHandler.class);
    //consturct
    public ListGamesHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.listGamesService = new ListGamesService(gameDAO, authDAO);
    }

    @Override
    //Handle
    public Object handle(Request req, Response res) {
        // Get the authorization token from the request headers
        String authToken = req.headers("Authorization");

        try {
            //service for listing
            ListGamesResult result = listGamesService.listGames(authToken);
            res.status(200); //OK
            return gson.toJson(result); //JSON object
        } catch (DataAccessException e) {
            //start logging error messages
            //LOGGER.error("Error during list games: {}", e.getMessage());
            String errorMessage = e.getMessage().toLowerCase();
            //LOGGER.error("Response error message: {}", errorMessage);
            if (errorMessage.contains("unauthorized")) {
                res.status(401);
            } else if (errorMessage.contains("game not found")) {
                res.status(404);
            } else if (errorMessage.contains("invalid player color")) {
                res.status(400);
            } else if (errorMessage.contains("player color already taken")) {
                res.status(409);
            } else {
                //System.out.println("Final check TEST");
                res.status(500);
                errorMessage = "Internal Server Error: " + errorMessage;
            }
            String responseBody = gson.toJson(new ResponseMessage("error: " + errorMessage));
            res.body(responseBody);
            //LOGGER.debug("Returning error response with message: {}", responseBody);
            return res.body();
        }
    }
    // Response message record class
    private record ResponseMessage(String message) {
    }
}
