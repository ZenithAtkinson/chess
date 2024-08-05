package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import request.JoinGameRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoinGameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private static final Logger LOGGER = LoggerFactory.getLogger(JoinGameService.class);

    public JoinGameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public void joinGame(JoinGameRequest request, String authToken) throws DataAccessException {
        //LOGGER.debug("Starting joinGame with request: {}, authToken: {}", request, authToken);

        // Get authentication data using the auth token
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData == null) {
            //LOGGER.error("Unauthorized access with token: {}", authToken);
            throw new DataAccessException("Unauthorized"); // Throw exception if unauthorized
        }

        // Get game data
        GameData gameData = gameDAO.getGame(request.getGameID());
        if (gameData == null) {
            //LOGGER.error("Game not found for ID: {}", request.getGameID());
            throw new DataAccessException("Game not found"); // Throw exception if game not found
        }

        String playerColor = request.getPlayerColor();
        if (playerColor == null || (!playerColor.equals("WHITE") && !playerColor.equals("BLACK"))) {
            //LOGGER.error("Invalid player color: {}", playerColor);
            throw new DataAccessException("Invalid player color");
        }

        // Check if the player color is already taken
        if (("WHITE".equals(playerColor) && gameData.getWhiteUsername() != null && !gameData.getWhiteUsername().isEmpty()) ||
                ("BLACK".equals(playerColor) && gameData.getBlackUsername() != null && !gameData.getBlackUsername().isEmpty())) {
            //LOGGER.error("Player color already taken: {}", playerColor);
            throw new DataAccessException("Player color already taken"); // Throw exception if player color is already taken
        }

        //Set the username for color
        if ("WHITE".equals(playerColor)) {
            gameData.setWhiteUsername(authData.getUsername());
        } else if ("BLACK".equals(playerColor)) { //check this
            gameData.setBlackUsername(authData.getUsername());
        }

        // Update the game data in the database
        gameDAO.updateGame(gameData);
        //LOGGER.debug("Successfully joined game with ID: {}", request.getGameID());
    }
}

