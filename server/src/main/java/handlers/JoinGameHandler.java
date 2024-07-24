package handlers;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import request.JoinGameRequest;
import service.JoinGameService;
import spark.Request;
import spark.Response;
import spark.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoinGameHandler implements Route {
    private final JoinGameService joinGameService;
    private final Gson gson = new Gson();
    private static final Logger LOGGER = LoggerFactory.getLogger(JoinGameHandler.class);

    public JoinGameHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.joinGameService = new JoinGameService(gameDAO, authDAO);
    }

    @Override
    public Object handle(Request req, Response res) {
        //Parse the join game request from the request body
        JoinGameRequest request = gson.fromJson(req.body(), JoinGameRequest.class);
        //Get the authorization token from the request headers
        String authToken = req.headers("Authorization");

        try {
            //LOG ALL THE ERRORS, for the test cases that don't work
            LOGGER.debug("Handling join game request: {}", request);
            // Call the service to join the game
            joinGameService.joinGame(request, authToken);
            res.status(200);
            return gson.toJson(new ResponseMessage("Game joined successfully"));
        } catch (DataAccessException e) {
            LOGGER.error("Error during join game: {}", e.getMessage());
            String errorMessage = e.getMessage();
            LOGGER.error("Response error message: {}", errorMessage);
            switch (errorMessage) {
                case "Unauthorized":
                    res.status(401);
                    break;
                case "Game not found":
                    res.status(400);
                    break;
                case "Invalid player color":
                    res.status(400);
                    break;
                case "Player color already taken":
                    res.status(403);  //Update status to 403 for the case
                    break;
                default:
                    res.status(500);
                    errorMessage = "Internal server error: " + errorMessage;
                    break;
            }
            String responseBody = gson.toJson(new ResponseMessage("error: " + errorMessage));
            res.body(responseBody);
            LOGGER.debug("Returning error response with message: {}", responseBody);
            return res.body();
        }
    }

    //Response message record class
    private record ResponseMessage(String message) {
    }
}
