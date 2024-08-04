package ui;

import java.util.Scanner;

import ServerUtils.ServerFacade;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;

public class PostLoginUI {
    private final ServerFacade serverFacade;
    private final AuthData authData;

    public PostLoginUI(ServerFacade serverFacade, AuthData authData) {
        this.serverFacade = serverFacade;
        this.authData = authData;
    }

    public void display() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Post-Login Commands: Help, Logout, Create Game, List Games, Play Game, Observe Game");
            String command = scanner.nextLine().trim().toLowerCase();
            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "logout":
                    logout();
                    return;
                case "create game":
                    createGame(scanner);
                    break;
                case "list games":
                    listGames();
                    break;
                case "play game":
                    playGame(scanner);
                    break;
                case "observe game":
                    observeGame(scanner);
                    break;
                default:
                    System.out.println("Unknown command. Type 'Help' for a list of commands.");
                    break;
            }
        }
    }

    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("Help - Displays this help message.");
        System.out.println("Logout - Logs out of the application.");
        System.out.println("Create Game - Creates a new game.");
        System.out.println("List Games - Lists all available games.");
        System.out.println("Play Game - Joins a game as a player.");
        System.out.println("Observe Game - Observes a game.");
    }

    private void logout() {
        System.out.println("Logging out...");
        // Implement the logout logic if needed
        new PreLoginUI(serverFacade).display();
    }

    private void createGame(Scanner scanner) {
        System.out.print("Game name: ");
        String gameName = scanner.nextLine().trim();

        CreateGameRequest gameRequest = new CreateGameRequest(gameName);
        try {
            serverFacade.createGame(gameRequest);
            System.out.println("Game created successfully!");
        } catch (Exception e) {
            System.out.println("Failed to create game: " + e.getMessage());
        }
    }

    private void listGames() {
        try {
            GameData[] games = serverFacade.listGames();
            if (games.length == 0) {
                System.out.println("No games available.");
            } else {
                System.out.println("Available games:");
                for (int i = 0; i < games.length; i++) {
                    GameData game = games[i];
                    System.out.printf("%d. %s (White: %s, Black: %s)%n", i + 1, game.getGameName(), game.getWhiteUsername(), game.getBlackUsername());
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to list games: " + e.getMessage());
        }
    }

    private void playGame(Scanner scanner) {
        listGames();
        System.out.print("Enter game number to join: ");
        int gameNumber = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Choose color (WHITE/BLACK): ");
        String color = scanner.nextLine().trim().toUpperCase();

        try {
            GameData[] games = serverFacade.listGames();
            if (gameNumber < 1 || gameNumber > games.length) {
                System.out.println("Invalid game number.");
                return;
            }

            GameData game = games[gameNumber - 1];
            GameData joinRequest = new GameData(game.getGameID(), color.equals("WHITE") ? authData.getUsername() : game.getWhiteUsername(), color.equals("BLACK") ? authData.getUsername() : game.getBlackUsername(), game.getGameName(), null, color);
            serverFacade.joinGame(joinRequest);
            System.out.println("Joined game successfully!");
        } catch (Exception e) {
            System.out.println("Failed to join game: " + e.getMessage());
        }
    }

    private void observeGame(Scanner scanner) {
        listGames();
        System.out.print("Enter game number to observe: ");
        int gameNumber = Integer.parseInt(scanner.nextLine().trim());

        try {
            GameData[] games = serverFacade.listGames();
            if (gameNumber < 1 || gameNumber > games.length) {
                System.out.println("Invalid game number.");
                return;
            }

            GameData game = games[gameNumber - 1];
            // Implement observe game logic if needed
            System.out.println("Observing game: " + game.getGameName());
        } catch (Exception e) {
            System.out.println("Failed to observe game: " + e.getMessage());
        }
    }
}
