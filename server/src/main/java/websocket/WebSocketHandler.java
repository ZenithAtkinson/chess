package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import dataaccess.GameDAO;
import model.GameData;
import model.AuthData;
import dataaccess.AuthDAO;

import java.io.IOException;
import java.util.logging.Logger;

@WebSocket
public class WebSocketHandler {
    private static final Logger LOGGER = Logger.getLogger(WebSocketHandler.class.getName());
    private static final WebSocketSessions SESSIONS = new WebSocketSessions();
    private static GameDAO gameDAO;
    private static AuthDAO authDAO;

    public static void initialize(GameDAO gameDAO, AuthDAO authDAO) {
        WebSocketHandler.gameDAO = gameDAO;
        WebSocketHandler.authDAO = authDAO;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        LOGGER.info("WebSocket connection opened: " + session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        LOGGER.info("WebSocket connection closed: " + session + ", Status: " + statusCode + ", Reason: " + reason);
        SESSIONS.removeSession(session);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        LOGGER.severe("WebSocket error: " + error.getMessage());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        LOGGER.info("Received message: " + message);
        try {
            Gson gson = new Gson();
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            LOGGER.info("Parsed command: " + command);
            handleCommand(session, command);
        } catch (Exception e) {
            LOGGER.severe("Failed to parse command: " + e.getMessage());
            sendErrorMessage(session, "Failed to parse command: " + e.getMessage());
        }
    }

    private void handleCommand(Session session, UserGameCommand command) {
        switch (command.getCommandType()) {
            case CONNECT:
                handleConnect(session, command);
                break;
            case MAKE_MOVE:
                handleMakeMove(session, command);
                break;
            case LEAVE:
                handleLeave(session, command);
                break;
            case RESIGN:
                handleResign(session, command);
                break;
            default:
                sendErrorMessage(session, "Invalid command type");
        }
    }

    private void handleConnect(Session session, UserGameCommand command) {
        LOGGER.info("Handling CONNECT command for session: " + session + ", command: " + command);
        try {
            AuthData authData = authDAO.getAuthData(command.getAuthToken());
            if (authData == null) {
                sendErrorMessage(session, "Invalid auth token");
                return;
            }

            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendErrorMessage(session, "Game not found");
                return;
            }

            SESSIONS.addSessionToGame(command.getGameID(), session);
            ServerMessage loadGameMessage = new ServerMessage(gameData.getChessGame());
            LOGGER.info("Sending LOAD_GAME message: " + loadGameMessage);
            SESSIONS.sendMessage(session, loadGameMessage);

            ServerMessage notifyMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    "User " + authData.getUsername() + " connected");
            SESSIONS.broadcastMessage(command.getGameID(), notifyMessage, session);

        } catch (Exception e) {
            LOGGER.severe("Failed to handle CONNECT command: " + e.getMessage());
            sendErrorMessage(session, "Failed to handle CONNECT command: " + e.getMessage());
        }
    }

    private void handleMakeMove(Session session, UserGameCommand command) {
        System.out.println("Handling MAKE_MOVE command for session: " + session + ", command: " + command);
        try {
            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendErrorMessage(session, "Game not found");
                return;
            }

            ChessGame chessGame = gameData.getChessGame();
            ChessMove move = command.getMove();
            if (move == null) {
                sendErrorMessage(session, "Invalid move");
                return;
            }

            String playerUsername = getUsernameFromAuthToken(command.getAuthToken());
            if (playerUsername == null) {
                sendErrorMessage(session, "Invalid auth token.");
                return;
            }

            // Check if the game has two active players
            if (gameData.getWhiteUsername() == null || gameData.getBlackUsername() == null) {
                sendErrorMessage(session, "Waiting for both players to join.");
                return;
            }

            // Ensure the player is part of the game and that the game is not over
            if (!isPlayerInGame(gameData, playerUsername)) {
                sendErrorMessage(session, "You are not a participant in this game or the game is over.");
                return;
            }

            // Check if it's the player's turn
            String currentTurnUsername = getCurrentTurnUsername(chessGame, gameData);
            if (!playerUsername.equals(currentTurnUsername)) {
                sendErrorMessage(session, "It's not your turn.");
                return;
            }

            // Validate and perform the move
            try {
                chessGame.makeMove(move);
            } catch (InvalidMoveException e) {
                sendErrorMessage(session, "Invalid move: " + e.getMessage());
                return;
            }

            // Update the game state
            gameDAO.updateGame(gameData);

            // Broadcast the updated game state
            ServerMessage loadGameMessage = new ServerMessage(chessGame);
            SESSIONS.broadcastMessage(command.getGameID(), loadGameMessage, null);

            // Notify about the move
            ServerMessage notificationMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    playerUsername + " made a move: " + move);
            SESSIONS.broadcastMessage(command.getGameID(), notificationMessage, session);

        } catch (Exception e) {
            System.err.println("Failed to handle MAKE_MOVE command: " + e.getMessage());
            sendErrorMessage(session, "Failed to handle MAKE_MOVE command: " + e.getMessage());
        }
    }

    private String getUsernameFromAuthToken(String authToken) throws DataAccessException {
        for (AuthData authData : authDAO.getAllAuthData()) {
            if (authData.getAuthToken().equals(authToken)) {
                return authData.getUsername();
            }
        }
        return null;
    }

    private boolean isPlayerInGame(GameData gameData, String username) {
        return (username.equals(gameData.getWhiteUsername()) || username.equals(gameData.getBlackUsername()))
                && gameData.getWhiteUsername() != null || gameData.getBlackUsername() != null;
    }

    private String getCurrentTurnUsername(ChessGame chessGame, GameData gameData) {
        return chessGame.getTeamTurn() == ChessGame.TeamColor.WHITE ? gameData.getWhiteUsername() : gameData.getBlackUsername();
    }


    private void handleLeave(Session session, UserGameCommand command) {
        LOGGER.info("Handling LEAVE command for session: " + session + ", command: " + command);
        try {
            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendErrorMessage(session, "Game not found");
                return;
            }

            // Fetch the username for the auth token
            String playerUsername = null;
            for (AuthData authData : authDAO.getAllAuthData()) {
                if (authData.getAuthToken().equals(command.getAuthToken())) {
                    playerUsername = authData.getUsername();
                    break;
                }
            }

            if (playerUsername == null) {
                sendErrorMessage(session, "Invalid auth token.");
                return;
            }

            // Check if the player is one of the game's participants
            boolean isParticipant = playerUsername.equals(gameData.getWhiteUsername()) || playerUsername.equals
                    (gameData.getBlackUsername());

            // Remove the player from the game
            if (playerUsername.equals(gameData.getWhiteUsername())) {
                gameData.setWhiteUsername(null);
            } else if (playerUsername.equals(gameData.getBlackUsername())) {
                gameData.setBlackUsername(null);
            }

            // Update the game state in the DAO
            gameDAO.updateGame(gameData);

            // Remove session from the game
            SESSIONS.removeSessionFromGame(command.getGameID(), session);

            // Create and send notification message
            if (isParticipant) {
                ServerMessage notificationMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        playerUsername + " has left the game.");
                SESSIONS.broadcastMessage(command.getGameID(), notificationMessage, session);
            } else {
                // Observer is leaving the game, only send a notification to remaining participants
                ServerMessage notificationMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        playerUsername + " is no longer observing the game.");
                SESSIONS.broadcastMessage(command.getGameID(), notificationMessage, session);
            }

            if (playerUsername.equals(gameData.getWhiteUsername())) {
                gameData.setWhiteUsername(null);
            } else if (playerUsername.equals(gameData.getBlackUsername())) {
                gameData.setBlackUsername(null);
            }

            // If both players have left, remove the game
            if (gameData.getWhiteUsername() == null && gameData.getBlackUsername() == null) {
                gameDAO.deleteGame(command.getGameID()); // Delete the game from the data store
            }

        } catch (Exception e) {
            LOGGER.severe("Failed to handle LEAVE command: " + e.getMessage());
            sendErrorMessage(session, "Failed to handle LEAVE command: " + e.getMessage());
        }
    }



    private void handleResign(Session session, UserGameCommand command) {
        System.out.println("Handling RESIGN command for session: " + session + ", command: " + command);
        try {
            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendErrorMessage(session, "Game not found");
                return;
            }

            ChessGame chessGame = gameData.getChessGame();

            // Fetch the username for the auth token
            String playerUsername = null;
            for (AuthData authData : authDAO.getAllAuthData()) {
                if (authData.getAuthToken().equals(command.getAuthToken())) {
                    playerUsername = authData.getUsername();
                    break;
                }
            }

            if (playerUsername == null) {
                sendErrorMessage(session, "Invalid auth token.");
                return;
            }

            // Check if the player is one of the game's participants
            if (!playerUsername.equals(gameData.getWhiteUsername()) && !playerUsername.equals
                    (gameData.getBlackUsername())) {
                sendErrorMessage(session, "You are not a participant in this game.");
                return;
            }

            // Check if the game is already over (either player has resigned)
            if (gameData.getWhiteUsername() == null || gameData.getBlackUsername() == null) {
                sendErrorMessage(session, "The game is already over.");
                return;
            }

            // Remove the player from the game
            if (playerUsername.equals(gameData.getWhiteUsername())) {
                gameData.setWhiteUsername(null);
            } else if (playerUsername.equals(gameData.getBlackUsername())) {
                gameData.setBlackUsername(null);
            }

            // Update the game state DAO
            gameDAO.updateGame(gameData);


            String opponentUsername = playerUsername.equals(gameData.getWhiteUsername()) ? gameData.getBlackUsername() :
                    gameData.getWhiteUsername();
            String resignationMessage = playerUsername + " has resigned. " + (opponentUsername != null ?
                    opponentUsername + " wins!" : "Game over.");

            // Broadcast the resignation message to all users
            ServerMessage notificationMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    resignationMessage);
            SESSIONS.broadcastMessage(command.getGameID(), notificationMessage, null);

            if (playerUsername.equals(gameData.getWhiteUsername())) {
                gameData.setWhiteUsername(null);
            } else if (playerUsername.equals(gameData.getBlackUsername())) {
                gameData.setBlackUsername(null);
            }

            // If both players have resigned or left, remove the game
            if (gameData.getWhiteUsername() == null && gameData.getBlackUsername() == null) {
                gameDAO.deleteGame(command.getGameID()); // Assuming removeGame deletes the game
            }

        } catch (Exception e) {
            System.err.println("Failed to handle RESIGN command: " + e.getMessage());
            sendErrorMessage(session, "Failed to handle RESIGN command: " + e.getMessage());
        }
    }



    private void sendErrorMessage(Session session, String errorMessage) {
        try {
            ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage);
            SESSIONS.sendMessage(session, error);
        } catch (IOException e) {
            LOGGER.severe("Failed to send error message: " + e.getMessage());
        }
    }
}
