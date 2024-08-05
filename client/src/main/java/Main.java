import ServerUtils.ServerFacade;
import ui.PreLoginUI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        int port = startServer();
        // Initialize ServerFacade with the server port
        ServerFacade serverFacade = new ServerFacade(port);

        // Initialize and display the PreLoginUI
        PreLoginUI preLoginUI = new PreLoginUI(serverFacade);
        preLoginUI.display();
    }

    private static int startServer() {
        try {
            // Command to run the server JAR
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "path/to/server.jar");
            Process process = processBuilder.start();

            // Capture the output from the server process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            int port = -1;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.startsWith("Server started on port: ")) {
                    port = Integer.parseInt(line.split(": ")[1]);
                }
            }

            if (port == -1) {
                throw new RuntimeException("Failed to start the server.");
            }

            return port;
        } catch (IOException e) {
            throw new RuntimeException("Failed to start the server.", e);
        }
    }
}
