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

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Login failed: Both Username and Password must be filled out.");
            return;
        }

        UserData loginRequest = new UserData(username, password, null);
        try {
            AuthData response = serverFacade.login(loginRequest);
            System.out.println("Login successful!");
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

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            System.out.println("Registration failed: All fields (Username, Password, Email) must be filled out.");
            return;
        }

        if (!isValidEmail(email)) {
            System.out.println("Registration failed: Invalid email format.");
            return;
        }

        UserData registerRequest = new UserData(username, password, email);
        try {
            AuthData response = serverFacade.register(registerRequest);
            System.out.println("Registration successful!");
            new PostLoginUI(serverFacade, response).display();
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        //Basic regex for email chekcer:
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$"; https://www.w3schools.com/java/java_regex.asp
        return email.matches(emailRegex);
    }
}
