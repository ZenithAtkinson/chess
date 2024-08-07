package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final WebSocketSessions sessions = new WebSocketSessions();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Connected: " + session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Closed: " + session + " with statusCode: " + statusCode + " and reason: " + reason);
        // Handle session cleanup if necessary
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error: " + session + " with error: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        Gson gson = new Gson();
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

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
    }

    private void connect(Session session, UserGameCommand command) throws IOException {
        sessions.addSessionToGame(command.getGameID(), session);
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, "Game loaded successfully");
        sessions.sendMessage(session, message);
    }

    private void makeMove(Session session, UserGameCommand command) throws IOException {
        // Implement game move logic here
        System.out.println("Move made: " + command);
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
