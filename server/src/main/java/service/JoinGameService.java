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
    private static final Logger logger = LoggerFactory.getLogger(JoinGameService.class);

    public JoinGameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public void joinGame(JoinGameRequest request, String authToken) throws DataAccessException {
        logger.debug("Starting joinGame with request: {}, authToken: {}", request, authToken);

        AuthData authData = authDAO.getAuthData(authToken);
        if (authData == null) {
            logger.error("Unauthorized access with token: {}", authToken);
            throw new DataAccessException("Unauthorized");
        }

        GameData gameData = gameDAO.getGame(request.getGameID());
        if (gameData == null) {
            logger.error("Game not found for ID: {}", request.getGameID());
            throw new DataAccessException("Game not found");
        }

        String playerColor = request.getPlayerColor();
        if (playerColor == null || (!playerColor.equals("WHITE") && !playerColor.equals("BLACK"))) {
            logger.error("Invalid player color: {}", playerColor);
            throw new DataAccessException("Invalid player color");
        }

        if (("WHITE".equals(playerColor) && gameData.getWhiteUsername() != null) ||
                ("BLACK".equals(playerColor) && gameData.getBlackUsername() != null)) {
            logger.error("Player color already taken: {}", playerColor);
            throw new DataAccessException("Player color already taken");
        }

        if ("WHITE".equals(playerColor)) {
            gameData.setWhiteUsername(authData.getUsername());
        } else if ("BLACK".equals(playerColor)) {
            gameData.setBlackUsername(authData.getUsername());
        }

        gameDAO.updateGame(gameData);
        logger.debug("Successfully joined game with ID: {}", request.getGameID());
    }
}
