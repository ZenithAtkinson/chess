package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.WebSocketSessions;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final WebSocketSessions webSocketSessions = new WebSocketSessions();
    private final Gson gson = new Gson();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("New connection: " + session.getRemoteAddress().getAddress());
        // Optionally handle new connection setup here
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Connection closed: " + session.getRemoteAddress().getAddress());
        webSocketSessions.removeSession(session);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error on connection: " + session.getRemoteAddress().getAddress());
        throwable.printStackTrace();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        try {
            handleCommand(session, command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleCommand(Session session, UserGameCommand command) throws IOException {
        switch (command.getCommandType()) {
            case CONNECT:
                connect(session, command);
                break;
            case MAKE_MOVE:
                makeMove(session, command);
                break;
            case LEAVE:
                leave(session, command);
                break;
            case RESIGN:
                resign(session, command);
                break;
        }
    }

    private void connect(Session session, UserGameCommand command) throws IOException {
        // Handle the CONNECT command logic
        webSocketSessions.addSession(command.getGameID(), session);
        // Send LOAD_GAME message to the root client
        ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        webSocketSessions.sendMessage(session, gson.toJson(loadGameMessage));
        // Notify other clients
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        webSocketSessions.broadcast(gson.toJson(notification), command.getGameID(), session);
    }

    private void makeMove(Session session, UserGameCommand command) throws IOException {
        // Handle the MAKE_MOVE command logic
        // Update game state and send LOAD_GAME and NOTIFICATION messages
    }

    private void leave(Session session, UserGameCommand command) throws IOException {
        // Handle the LEAVE command logic
        webSocketSessions.removeSession(command.getGameID(), session);
        // Notify other clients
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        webSocketSessions.broadcast(gson.toJson(notification), command.getGameID(), session);
    }

    private void resign(Session session, UserGameCommand command) throws IOException {
        // Handle the RESIGN command logic
        // Mark game as over and send NOTIFICATION messages
    }
}
