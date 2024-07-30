package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Initializes the database schema by creating necessary tables.
     */
    public static void initializeDatabase() throws DataAccessException {
        try (Connection conn = getConnection()) {
            try (
                    PreparedStatement dropMoves = conn.prepareStatement("DROP TABLE IF EXISTS moves");
                    PreparedStatement dropAuthTokens = conn.prepareStatement("DROP TABLE IF EXISTS auth_tokens");
                    PreparedStatement dropGames = conn.prepareStatement("DROP TABLE IF EXISTS games");
                    PreparedStatement dropUsers = conn.prepareStatement("DROP TABLE IF EXISTS users");
                    PreparedStatement createUsers = conn.prepareStatement(
                            "CREATE TABLE users (" +
                                    "username VARCHAR(255) PRIMARY KEY, " +
                                    "password VARCHAR(255) NOT NULL, " +
                                    "email VARCHAR(255) NOT NULL" +
                                    ")");
                    PreparedStatement createAuthTokens = conn.prepareStatement(
                            "CREATE TABLE auth_tokens (" +
                                    "token VARCHAR(255) PRIMARY KEY, " +
                                    "username VARCHAR(255), " +
                                    "FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE" +
                                    ")");
                    PreparedStatement createGames = conn.prepareStatement(
                            "CREATE TABLE games (" +
                                    "id INT PRIMARY KEY, " +
                                    "whiteUsername VARCHAR(255), " +
                                    "blackUsername VARCHAR(255), " +
                                    "name VARCHAR(255), " +
                                    "gameState TEXT, " +
                                    "additionalParameter VARCHAR(255), " +
                                    "FOREIGN KEY (whiteUsername) REFERENCES users(username) ON DELETE SET NULL, " +
                                    "FOREIGN KEY (blackUsername) REFERENCES users(username) ON DELETE SET NULL" +
                                    ")");
                    PreparedStatement createMoves = conn.prepareStatement(
                            "CREATE TABLE moves (" +
                                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                                    "gameId INT, " +
                                    "move VARCHAR(255), " +
                                    "FOREIGN KEY (gameId) REFERENCES games(id) ON DELETE CASCADE" +
                                    ")")
            ) {
                // Drop tables in correct order
                dropMoves.executeUpdate();
                dropAuthTokens.executeUpdate();
                dropGames.executeUpdate();
                dropUsers.executeUpdate();

                // Create tables in correct order
                createUsers.executeUpdate();
                createAuthTokens.executeUpdate();
                createGames.executeUpdate();
                createMoves.executeUpdate();

            } catch (SQLException e) {
                throw new DataAccessException("Error initializing database", e);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error establishing database connection", e);
        }
    }

    /**
     * Initializes the database by calling the method to create the database and then setting up the schema.
     */
    public void initialize() {
        try {
            createDatabase();
            initializeDatabase();
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }
}
