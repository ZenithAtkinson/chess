package dataaccess;

import model.UserData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SQLUserDAO implements UserDAO {
    private final Map<String, UserData> memoryUserData = new HashMap<>();

    public SQLUserDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            System.err.println("Error configuring database: " + e.getMessage());
        }
    }

    private void configureDatabase() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS Users (" +
                    "username VARCHAR(255) PRIMARY KEY, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "email VARCHAR(255) NOT NULL)";
            stmt.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while configuring the database");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData user = null;
        String sql = "SELECT * FROM Users WHERE username = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new UserData(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Falling back to in-memory storage: " + e.getMessage());
            user = memoryUserData.get(username);
        }

        return user;
    }

    @Override
    public boolean addUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO Users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Falling back to in-memory storage: " + e.getMessage());
            memoryUserData.put(user.getUsername(), user);
            return true;
        }
    }

    @Override
    public boolean updateUser(UserData user) throws DataAccessException {
        String sql = "UPDATE Users SET password = ?, email = ? WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getPassword());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getUsername());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Falling back to in-memory storage: " + e.getMessage());
            memoryUserData.put(user.getUsername(), user);
            return true;
        }
    }

    @Override
    public boolean deleteUser(String username) throws DataAccessException {
        String sql = "DELETE FROM Users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Falling back to in-memory storage: " + e.getMessage());
            memoryUserData.remove(username);
            return true;
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Users";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Falling back to in-memory storage: " + e.getMessage());
            memoryUserData.clear();
        }
    }

    @Override
    public Collection<UserData> getAllUsers() throws DataAccessException {
        Collection<UserData> users = new ArrayList<>();
        String sql = "SELECT * FROM Users";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UserData user = new UserData(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Falling back to in-memory storage: " + e.getMessage());
            users.addAll(memoryUserData.values());
        }
        return users;
    }
}
