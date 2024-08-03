package ui;
// Play Game and Observe should both call "BoardPrinter"
import java.util.Scanner;
import ServerUtils.ServerFacade;
import chess.ChessGame;
import



public class PostLoginUI {
    private static Scanner scanner = new Scanner(System.in);
    private static ServerFacade serverFacade = new ServerFacade();

    public static void displayMenu() {
        System.out.print("[LOGGED_IN] >>> ");
        while (true) {
            String[] input = scanner.nextLine().split(" ");
            String command = input[0].toLowerCase();

            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "logout":
                    logout();
                    break;
                case "create":
                    if (input.length == 2) {
                        createGame(input[1]);
                    } else {
                        System.out.println("Usage: create <NAME>");
                    }
                    break;
                case "list":
                    listGames();
                    break;
                case "join":
                    if (input.length == 3) {
                        joinGame(input[1], input[2]);
                    } else {
                        System.out.println("Usage: join <ID> [WHITE|BLACK]");
                    }
                    break;
                case "observe":
                    if (input.length == 2) {
                        observeGame(input[1]);
                    } else {
                        System.out.println("Usage: observe <ID>");
                    }
                    break;
                case "quit":
                    quit();
                    break;
                default:
                    System.out.println("Invalid command. Type 'help' for available commands.");
            }
            System.out.print("[LOGGED_IN] >>> ");
        }
    }

    public static void displayHelp() {
        System.out.println("help - with possible commands");
        System.out.println("logout - when you are done");
        System.out.println("create <NAME> - a game");
        System.out.println("list - games");
        System.out.println("join <ID> [WHITE|BLACK] - a game");
        System.out.println("observe <ID> - a game");
        System.out.println("quit - playing chess");
    }

    public static void logout() {
        try {
            String authToken = DataCache.getInstance().getAuthToken();
            serverFacade.logout(authToken);
            System.out.println("Logged out successfully.");
            PreLoginUI.displayWelcomeMessage();
            PreLoginUI.displayMenu();
        } catch (Exception e) {
            System.out.println("Error logging out: " + e.getMessage());
        }
    }

    public static void createGame(String gameName) {
        try {
            Game game = new Game(gameName);
            serverFacade.createGame(game);
            System.out.println("Game created successfully!");
        } catch (Exception e) {
            System.out.println("Error creating game: " + e.getMessage());
        }
    }

    public static void listGames() {
        try {
            List<Game> games = serverFacade.listGames();
            int index = 1;
            for (Game game : games) {
                System.out.println(index + ". " + game.getName() + " - Players: " + game.getPlayers());
                index++;
            }
        } catch (Exception e) {
            System.out.println("Error listing games: " + e.getMessage());
        }
    }

    public static void joinGame(String gameId, String color) {
        try {
            serverFacade.joinGame(gameId, color);
            System.out.println("Joined game successfully!");
        } catch (Exception e) {
            System.out.println("Error joining game: " + e.getMessage());
        }
    }

    public static void observeGame(String gameId) {
        // Implement observe game logic (Phase 6)
    }

    public static void quit() {
        System.out.println("Exiting the game...");
        System.exit(0);
    }
}
