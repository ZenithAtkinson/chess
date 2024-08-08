package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
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
    private static final Logger logger = Logger.getLogger(WebSocketHandler.class.getName());
    private static final WebSocketSessions sessions = new WebSocketSessions();
    private static GameDAO gameDAO;
    private static AuthDAO authDAO;

    public static void initialize(GameDAO gameDAO, AuthDAO authDAO) {
        WebSocketHandler.gameDAO = gameDAO;
        WebSocketHandler.authDAO = authDAO;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        logger.info("WebSocket connection opened: " + session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        logger.info("WebSocket connection closed: " + session + ", Status: " + statusCode + ", Reason: " + reason);
        sessions.removeSession(session);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        logger.severe("WebSocket error: " + error.getMessage());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        logger.info("Received message: " + message);
        try {
            Gson gson = new Gson();
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            logger.info("Parsed command: " + command);
            handleCommand(session, command);
        } catch (Exception e) {
            logger.severe("Failed to parse command: " + e.getMessage());
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
        logger.info("Handling CONNECT command for session: " + session + ", command: " + command);
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

            sessions.addSessionToGame(command.getGameID(), session);
            ServerMessage loadGameMessage = new ServerMessage(gameData.getChessGame());
            logger.info("Sending LOAD_GAME message: " + loadGameMessage);
            sessions.sendMessage(session, loadGameMessage);

            ServerMessage notifyMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "User " + authData.getUsername() + " connected");
            sessions.broadcastMessage(command.getGameID(), notifyMessage, session);

        } catch (Exception e) {
            logger.severe("Failed to handle CONNECT command: " + e.getMessage());
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

            // Check if the player is one of the game's participants and if the game is active
            if ((!playerUsername.equals(gameData.getWhiteUsername()) && !playerUsername.equals(gameData.getBlackUsername())) ||
                    gameData.getWhiteUsername() == null || gameData.getBlackUsername() == null) {
                sendErrorMessage(session, "It's not your turn or the game is over.");
                return;
            }

            // Check if it's the player's turn
            String currentTurnUsername = chessGame.getTeamTurn() == ChessGame.TeamColor.WHITE ? gameData.getWhiteUsername() : gameData.getBlackUsername();
            if (!playerUsername.equals(currentTurnUsername)) {
                sendErrorMessage(session, "It's not your turn.");
                return;
            }

            // Validate the move
            try {
                chessGame.makeMove(move);
            } catch (InvalidMoveException e) {
                sendErrorMessage(session, "Invalid move: " + e.getMessage());
                return;
            }

            // Update the game state in the DAO
            gameDAO.updateGame(gameData);

            // Create and send LOAD_GAME message to all sessions in the game
            ServerMessage loadGameMessage = new ServerMessage(chessGame);
            sessions.broadcastMessage(command.getGameID(), loadGameMessage, null);

            // Create and send a notification message about the move
            ServerMessage notificationMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Move made: " + move);
            sessions.broadcastMessage(command.getGameID(), notificationMessage, session);

        } catch (Exception e) {
            System.err.println("Failed to handle MAKE_MOVE command: " + e.getMessage());
            sendErrorMessage(session, "Failed to handle MAKE_MOVE command: " + e.getMessage());
        }
    }
    private void handleLeave(Session session, UserGameCommand command) {
        logger.info("Handling LEAVE command for session: " + session + ", command: " + command);
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
            boolean isParticipant = playerUsername.equals(gameData.getWhiteUsername()) || playerUsername.equals(gameData.getBlackUsername());

            // Remove the player from the game
            if (playerUsername.equals(gameData.getWhiteUsername())) {
                gameData.setWhiteUsername(null);
            } else if (playerUsername.equals(gameData.getBlackUsername())) {
                gameData.setBlackUsername(null);
            }

            // Update the game state in the DAO
            gameDAO.updateGame(gameData);

            // Remove session from the game
            sessions.removeSessionFromGame(command.getGameID(), session);

            // Create and send notification message
            if (isParticipant) {
                ServerMessage notificationMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, playerUsername + " has left the game.");
                sessions.broadcastMessage(command.getGameID(), notificationMessage, session);
            } else {
                // Observer is leaving the game, only send a notification to remaining participants
                ServerMessage notificationMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, playerUsername + " is no longer observing the game.");
                sessions.broadcastMessage(command.getGameID(), notificationMessage, session);
            }

        } catch (Exception e) {
            logger.severe("Failed to handle LEAVE command: " + e.getMessage());
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
            if (!playerUsername.equals(gameData.getWhiteUsername()) && !playerUsername.equals(gameData.getBlackUsername())) {
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

            // Update the game state in the DAO
            gameDAO.updateGame(gameData);

            // Create a consolidated resignation message
            String opponentUsername = playerUsername.equals(gameData.getWhiteUsername()) ? gameData.getBlackUsername() : gameData.getWhiteUsername();
            String resignationMessage = playerUsername + " has resigned. " + (opponentUsername != null ? opponentUsername + " wins!" : "Game over.");

            // Broadcast the resignation message to all users
            ServerMessage notificationMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, resignationMessage);
            sessions.broadcastMessage(command.getGameID(), notificationMessage, null);

        } catch (Exception e) {
            System.err.println("Failed to handle RESIGN command: " + e.getMessage());
            sendErrorMessage(session, "Failed to handle RESIGN command: " + e.getMessage());
        }
    }



    private void sendErrorMessage(Session session, String errorMessage) {
        try {
            ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage);
            sessions.sendMessage(session, error);
        } catch (IOException e) {
            logger.severe("Failed to send error message: " + e.getMessage());
        }
    }
}
