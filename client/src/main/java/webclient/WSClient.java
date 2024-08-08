package webclient;

import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WSClient implements MessageHandler.Whole<String> {

    private Session session;
    private final Gson gson = new Gson();

    public WSClient(String host, int port) throws URISyntaxException, DeploymentException, IOException {
        URI uri = new URI("ws://" + host + ':' + port + "/ws");

        session = ContainerProvider.getWebSocketContainer().connectToServer(new Endpoint() {
            @Override
            public void onOpen(Session session, EndpointConfig endpointConfig) {
                System.out.println("WebSocket connection opened.");
            }
        }, uri);

        session.addMessageHandler(this);

        System.out.println("WebSocket connection established.");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received: " + message); // MUST BE REMOVED ***********************

        try {
            ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

            switch (serverMessage.getServerMessageType()) {
                case LOAD_GAME:
                    handleLoadGame(serverMessage.getGame());
                    break;
                case ERROR:
                    handleError(serverMessage.getErrorMessage());
                    break;
                case NOTIFICATION:
                    handleNotification(serverMessage.getMessage());
                    break;
                default:
                    System.out.println("Unknown message type received.");
            }
        } catch (Exception e) {
            System.out.println("Failed to parse server message: " + e.getMessage());
        }
    }

    private void handleLoadGame(Object game) {
        System.out.println("Loading game...");
        // Add logic to handle loading the game state
    }

    private void handleError(String errorMessage) {
        System.err.println("Error: " + errorMessage);
        // Add logic to handle errors
    }

    private void handleNotification(String message) {
        System.out.println("Notification: " + message);
        // Add logic to handle notifications
    }

    public void send(String msg) throws IOException {
        System.out.println("Sending: " + msg); // MUST BE REMOVED *******************
        session.getBasicRemote().sendText(msg);
    }

    public void close() throws IOException {
        System.out.println("Closing WebSocket connection.");
        session.close();
    }

    public void connectAsPlayer(String authToken, int gameId, String username) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameId, null, username);
        send(gson.toJson(command));
    }

    public void connectAsObserver(String authToken, int gameId, String username) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameId, null, username);
        send(gson.toJson(command));
    }

    public void makeMove(String authToken, int gameId, ChessMove move, String username) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameId, move, username);
        send(gson.toJson(command));
    }

    public void leave(String authToken, int gameId, String username) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameId, null, username);
        send(gson.toJson(command));
    }

    public void resign(String authToken, int gameId, String username) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameId, null, username);
        send(gson.toJson(command));
    }

    public void onError(Session session, Throwable thr) {
        System.err.println("WebSocket error: " + thr.getMessage());
    }

    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket connection closed: " + closeReason.getReasonPhrase());
    }
}
