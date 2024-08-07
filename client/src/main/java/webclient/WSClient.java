package webclient;

import com.google.gson.Gson;
import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;

@ClientEndpoint
public class WSClient {

    private Session session;
    private Gson gson = new Gson();

    public static void main(String[] args) throws Exception {
        WSClient wsClient = new WSClient();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter command (move, leave, resign, connect_player, connect_observer): ");
            String command = scanner.nextLine().trim();
            switch (command) {
                case "move":
                    System.out.println("Enter move (e.g., e2 e4): ");
                    String move = scanner.nextLine().trim();
                    wsClient.makeMove(move);
                    break;
                case "leave":
                    wsClient.leave();
                    break;
                case "resign":
                    wsClient.resign();
                    break;
                case "connect_player":
                    System.out.println("Enter game ID and color (WHITE/BLACK): ");
                    int gameId = scanner.nextInt();
                    String color = scanner.next().trim();
                    wsClient.connectAsPlayer(gameId, color);
                    break;
                case "connect_observer":
                    System.out.println("Enter game ID: ");
                    int gameIdObs = scanner.nextInt();
                    wsClient.connectAsObserver(gameIdObs);
                    break;
                default:
                    System.out.println("Unknown command");
            }
        }
    }

    public WSClient() throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                System.out.println("Received: " + message);
            }
        });
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        System.out.println("Connected to server");
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Disconnected from server: " + closeReason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void makeMove(String move) throws Exception {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, "authToken", 1, move);
        send(gson.toJson(command));
    }

    public void leave() throws Exception {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, "authToken", 1, null);
        send(gson.toJson(command));
    }

    public void resign() throws Exception {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, "authToken", 1, null);
        send(gson.toJson(command));
    }

    public void connectAsPlayer(int gameId, String color) throws Exception {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, "authToken", gameId, color);
        send(gson.toJson(command));
    }

    public void connectAsObserver(int gameId) throws Exception {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, "authToken", gameId, "observer");
        send(gson.toJson(command));
    }

    //Define UserGameCommand class to match the expected JSON structure?
    private class UserGameCommand {
        private CommandType commandType;
        private String authToken;
        private int gameId;
        private String move;

        public UserGameCommand(CommandType commandType, String authToken, int gameId, String move) {
            this.commandType = commandType;
            this.authToken = authToken;
            this.gameId = gameId;
            this.move = move;
        }

        public enum CommandType {
            CONNECT,
            MAKE_MOVE,
            LEAVE,
            RESIGN
        }
    }
}
