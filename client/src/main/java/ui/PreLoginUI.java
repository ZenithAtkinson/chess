package ui;

import java.util.Scanner;
import ServerUtils.ServerFacade;
import model.UserData;
import model.AuthData;

public class PreLoginUI {
    private static Scanner scanner = new Scanner(System.in);
    private static ServerFacade serverFacade = new ServerFacade();

    public static void displayWelcomeMessage() {
        System.out.println("🏰 Welcome to 240 chess. Type Help to get started. 🏰");
    }

    public static void displayMenu() {
        System.out.print("[LOGGED_OUT] >>> ");
    }

    public static String getUserInput() {
        return scanner.nextLine();
    }

    public static void displayHelp() {
        System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
        System.out.println("login <USERNAME> <PASSWORD> - to play chess");
        System.out.println("quit - playing chess");
        System.out.println("help - with possible commands");
    }

    public static void quit() {
        System.out.println("Exiting the program...");
        System.exit(0);
    }

    public static void login(String username, String password) {
        try {
            UserData userData = new UserData(username, password, null);
            AuthData authData = serverFacade.login(userData);
            if (authData != null) {
                System.out.println("Login successful!");
                PostLoginUI.displayMenu();
            } else {
                System.out.println("Login failed. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error logging in: " + e.getMessage());
        }
    }

    public static void register(String username, String password, String email) {
        try {
            UserData userData = new UserData(username, password, email);
            AuthData authData = serverFacade.register(userData);
            if (authData != null) {
                System.out.println("Registration successful! Logging in...");
                PostLoginUI.displayMenu();
            } else {
                System.out.println("Registration failed. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error registering: " + e.getMessage());
        }
    }
}
