package ui;

import java.util.List;
import java.util.Scanner;

import serverutils.ServerFacade;
import chess.ChessBoard;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;

public class PostLoginUI {
    private final ServerFacade serverFacade;
    private final AuthData authData;
    private final BoardPrinter boardPrinter;

    public PostLoginUI(ServerFacade serverFacade, AuthData authData) {
        this.serverFacade = serverFacade;
        this.authData = authData;
        this.boardPrinter = new BoardPrinter();
    }

    //REMEMBER: Escape sequences for flavor text.
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
        new PreLoginUI(serverFacade).display();
    }

    private void createGame(Scanner scanner) {
        System.out.print("Game name: ");
        String gameName = scanner.nextLine().trim();

        if (gameName.isEmpty()) {
            System.out.println("Failed to create game: Game name cannot be empty.");
            return;
        }

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
            List<GameData> games = serverFacade.listGames();
            if (games.isEmpty()) {
                System.out.println("No games available.");
            } else {
                System.out.println("Available games:");
                for (int i = 0; i < games.size(); i++) {
                    GameData game = games.get(i);
                    //Double check formatting(?)
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
        try {
            int gameNumber = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Choose color (WHITE/BLACK): ");
            String color = scanner.nextLine().trim().toUpperCase();

            if (!color.equals("WHITE") && !color.equals("BLACK")) {
                System.out.println("Invalid color choice. Please enter WHITE or BLACK.");
                return;
            }

            List<GameData> games = serverFacade.listGames();
            if (gameNumber < 1 || gameNumber > games.size()) {
                System.out.println("Invalid game number.");
                return;
            }

            GameData game = games.get(gameNumber - 1);
            JoinGameRequest joinRequest = new JoinGameRequest(game.getGameID(), color);
            serverFacade.joinGame(joinRequest);
            System.out.println("Joined game successfully!");

            // Print chessboard
            ChessBoard board = new ChessBoard();
            board.resetBoard();
            System.out.println("Chessboard from White's perspective:");
            boardPrinter.printBoard(board);
            System.out.println("Chessboard from Black's perspective:");
            boardPrinter.printBoardReversed(board);

        } catch (NumberFormatException e) {
            System.out.println("Invalid game number. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Failed to join game: " + e.getMessage());
        }
    }

    private void observeGame(Scanner scanner) {
        listGames();
        System.out.print("Enter game number to observe: ");
        try {
            int gameNumber = Integer.parseInt(scanner.nextLine().trim());

            List<GameData> games = serverFacade.listGames();
            if (gameNumber < 1 || gameNumber > games.size()) {
                System.out.println("Invalid game number.");
                return;
            }

            GameData game = games.get(gameNumber - 1);
            //Additional observe game fields.?
            System.out.println("Observing game: " + game.getGameName());

            //Print  chessboard
            ChessBoard board = new ChessBoard();
            board.resetBoard();
            System.out.println("Chessboard from White's perspective:");
            boardPrinter.printBoard(board);
            System.out.println("Chessboard from Black's perspective:");
            boardPrinter.printBoardReversed(board);

        } catch (NumberFormatException e) {
            System.out.println("Invalid game number. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Failed to observe game: " + e.getMessage());
        }
    }
}
