package dataaccess;

import model.UserData;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLUserDAO implements UserDAO {

    private static final String CREATE_TABLE_STATEMENT = getCreateStatement();

    //Modify to be like SQLGameDAO
    private static String getCreateStatement() {
        return """
            CREATE TABLE IF NOT EXISTS `users` (
                `username` VARCHAR(64) NOT NULL PRIMARY KEY,
                `password` VARCHAR(64) NOT NULL,
                `email` VARCHAR(64) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """;
    }

    public SQLUserDAO() {
        try {
            configureUSERDatabase();
        } catch (DataAccessException e) {
            System.out.println("User database unable to be configured: " + e.getMessage());
        }
    }

    private void configureUSERDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var connections = DatabaseManager.getConnection()) {
            var statements = new String[]{CREATE_TABLE_STATEMENT};
            for (var statement : statements) {
                try (var preparedStatement = connections.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException exec) {
            throw new DataAccessException(String.format("Unable to configure the user database: %s", exec.getMessage()));
        }
    }

    @Override
    public boolean addUser(UserData userData) throws DataAccessException {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userData.getUsername());
            stmt.setString(2, userData.getPassword());
            stmt.setString(3, userData.getEmail());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Adding user failed, no rows affected.");
            }
            return true;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) { // SQL state for unique constraint violation
                throw new DataAccessException("User already exists");
            } else {
                throw new DataAccessException("Error adding user", e);
            }
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting user", e);
        }
        return null;
    }

    @Override
    public boolean updateUser(UserData userData) throws DataAccessException {
        String sql = "UPDATE users SET password = ?, email = ? WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userData.getPassword());
            stmt.setString(2, userData.getEmail());
            stmt.setString(3, userData.getUsername());
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating user", e);
        }
    }

    @Override
    public boolean deleteUser(String username) throws DataAccessException {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting user", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM users";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing users", e);
        }
    }

    @Override
    public List<UserData> getAllUsers() throws DataAccessException {
        List<UserData> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new UserData(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting all users", e);
        }
        return users;
    }
}
