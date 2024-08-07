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
        // Handle new connection ( if needed )
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        // Handle closing connection ( if needed )
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        // Handle error ( if needed )
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
        // connect logic
        sessions.addSessionToGame(command.getGameID(), session);
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, "Game loaded successfully");
        sessions.sendMessage(session, message);
    }

    private void makeMove(Session session, UserGameCommand command) throws IOException {
        // make move logic
    }

    private void leaveGame(Session session, UserGameCommand command) throws IOException {
        // leave game logic
        sessions.removeSessionFromGame(command.getGameID(), session);
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Player left the game");
        sessions.broadcastMessage(command.getGameID(), message, session);
    }

    private void resignGame(Session session, UserGameCommand command) throws IOException {
        // resign game logic
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Player resigned the game");
        sessions.broadcastMessage(command.getGameID(), message, session);
    }
}
