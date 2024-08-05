package ui;

import java.util.Scanner;

import ServerUtils.ServerFacade;
import model.UserData;
import model.AuthData;

import static ui.EscapeSequences.*;

public class PreLoginUI {
    private final ServerFacade serverFacade;

    public PreLoginUI(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void display() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Chess 240. Type, " + SET_TEXT_BOLD  + SET_TEXT_UNDERLINE + "\"Help\""+RESET_TEXT_UNDERLINE + RESET_TEXT_BOLD_FAINT+", to get started.");
        while (true) {
            //System.out.println("Pre-Login Commands: Help, Quit, Login, Register");
            String command = scanner.nextLine().trim().toLowerCase();
            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "quit":
                    System.out.println("Goodbye!");
                    return;
                case "login":
                    login(scanner);
                    break;
                case "register":
                    register(scanner);
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
        System.out.println("Quit - Exits the program.");
        System.out.println("Login - Logs into the application.");
        System.out.println("Register - Registers a new user.");
    }

    private void login(Scanner scanner) {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        UserData loginRequest = new UserData(username, password, null);
        try {
            AuthData response = serverFacade.login(loginRequest);
            System.out.println("Login successful! Auth token: " + response.getAuthToken());
            new PostLoginUI(serverFacade, response).display();
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void register(Scanner scanner) {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        UserData registerRequest = new UserData(username, password, email);
        try {
            AuthData response = serverFacade.register(registerRequest);
            System.out.println("Registration successful! Auth token: " + response.getAuthToken());
            new PostLoginUI(serverFacade, response).display();
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }
}
