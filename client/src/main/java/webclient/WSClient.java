package webclient;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
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
    private ChessBoard board; // Holds the current state of the board

    public WSClient(String host, int port) throws URISyntaxException, DeploymentException, IOException {
        URI uri = new URI("ws://" + host + ':' + port + "/ws");

        session = ContainerProvider.getWebSocketContainer().connectToServer(new Endpoint() {
            @Override
            public void onOpen(Session session, EndpointConfig endpointConfig) {
                //Connection OPEN
            }
        }, uri);

        session.addMessageHandler(this);

        // add LOGGER to confirm websocket establishment
    }

    @Override
    public void onMessage(String message) {
        try {
            ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

            switch (serverMessage.getServerMessageType()) {
                case LOAD_GAME:
                    handleLoadGame(serverMessage.getGame().getBoard());
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

    private void handleLoadGame(ChessBoard gameBoard) {
        // Update the local chess board with the game state received from the server
        this.board = gameBoard;
        redrawBoard(); // Call to redraw the board
        System.out.println("Game loaded successfully.");
    }

    public void redrawBoard() {
        if (this.board != null) {
            // Logic to redraw the board based on the current state
            // You can print the board or pass it to a UI component for rendering
            System.out.println("Redrawing board...");
            // Implement board drawing logic here, e.g., System.out.println(this.board);
                //confirm with specs
        }
    }

    public void handleError(String errorMessage) {
        //Print
        System.err.println("Error: " + errorMessage);
    }

    public void handleNotification(String message) {
        //Print notif
        System.out.println("Notification: " + message);
    }

    public void send(String msg) throws IOException {
        session.getBasicRemote().sendText(msg);
    }

    public void close() throws IOException {
    // I ned to add this method
        session.close();
    }

    public void connectAsPlayer(String authToken, int gameId, String username) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameId, null, username);
        send(gson.toJson(command));
    }

    public void connectAsObserver(String authToken, int gameId, String username) throws IOException {
        connectAsPlayer(authToken, gameId, username);
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
}
