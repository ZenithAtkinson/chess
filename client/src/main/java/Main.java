import ui.BoardPrinter;
import chess.ChessBoard;
import server.Server;
import ServerUtils.ServerFacade;
import ui.PreLoginUI;

public class Main {
    public static void main(String[] args) {
        // Start the server
        Server server = new Server();
        int port = server.run(0); // 0 means a random available port is assigned
        System.out.println("Server started on port: " + port);

        // Initialize ServerFacade with the server port
        ServerFacade serverFacade = new ServerFacade(port);

        // Initialize and display the PreLoginUI
        PreLoginUI preLoginUI = new PreLoginUI(serverFacade);
        preLoginUI.display();

    }
}
