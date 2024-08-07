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
            if (gameData != null) {
                ChessGame game = gameData.getChessGame();
                String currentTurnUsername = game.getTeamTurn() == ChessGame.TeamColor.WHITE
                        ? gameData.getWhiteUsername() : gameData.getBlackUsername();

                if (!command.getAuthToken().equals(currentTurnUsername)) {
                    sendErrorMessage(session, "It's not your turn or you are not a participant in this game");
                    return;
                }

                try {
                    game.makeMove(command.getMove());
                    gameDAO.updateGame(gameData);
                    ServerMessage loadGameMessage = new ServerMessage(game);
                    sessions.broadcastMessage(command.getGameID(), loadGameMessage, null);
                } catch (InvalidMoveException e) {
                    sendErrorMessage(session, "Invalid move: " + e.getMessage());
                }
            } else {
                sendErrorMessage(session, "Game not found");
            }
        } catch (Exception e) {
            System.err.println("Failed to handle MAKE_MOVE command: " + e.getMessage());
            sendErrorMessage(session, "Failed to handle MAKE_MOVE command: " + e.getMessage());
        }
    }




    private void handleLeave(Session session, UserGameCommand command) {
        logger.info("Handling LEAVE command for session: " + session + ", command: " + command);
        sessions.removeSessionFromGame(command.getGameID(), session);
        ServerMessage notificationMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Player left the game");
        try {
            sessions.broadcastMessage(command.getGameID(), notificationMessage, session);
        } catch (IOException e) {
            logger.severe("Failed to send leave notification: " + e.getMessage());
        }
    }

    private void handleResign(Session session, UserGameCommand command) {
        // Implement resign handling logic
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
