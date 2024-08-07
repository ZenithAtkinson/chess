package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import dataaccess.*;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final WebSocketSessions sessions = new WebSocketSessions();
    private final Gson gson = new Gson();
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public WebSocketHandler(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("WebSocket connection opened: " + session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("WebSocket connection closed: " + session + ", Status: " + statusCode + ", Reason: " + reason);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.println("Received message: " + message);

        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            System.out.println("Parsed command: " + command);

            switch (command.getCommandType()) {
                case CONNECT:
                    connect(session, command);
                    break;
                case MAKE_MOVE:
                    makeMove(session, command);
                    break;
                case LEAVE:
                    leaveGame(session, command);
                    break;
                case RESIGN:
                    resignGame(session, command);
                    break;
                default:
                    sessions.sendMessage(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Invalid command type"));
                    break;
            }
        } catch (Exception e) {
            System.err.println("Failed to parse command: " + e.getMessage());
            e.printStackTrace();
            sessions.sendMessage(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Incorrect variable type"));
        }
    }

    private void connect(Session session, UserGameCommand command) throws IOException {
        sessions.addSessionToGame(command.getGameID(), session);
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, "Game loaded successfully");
        sessions.sendMessage(session, message);
    }

    private void makeMove(Session session, UserGameCommand command) throws IOException {
        // Add logic to handle the move
        System.out.println("Making move: " + command);
    }

    private void leaveGame(Session session, UserGameCommand command) throws IOException {
        sessions.removeSessionFromGame(command.getGameID(), session);
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Player left the game");
        sessions.broadcastMessage(command.getGameID(), message, session);
    }

    private void resignGame(Session session, UserGameCommand command) throws IOException {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Player resigned the game");
        sessions.broadcastMessage(command.getGameID(), message, session);
    }
}
