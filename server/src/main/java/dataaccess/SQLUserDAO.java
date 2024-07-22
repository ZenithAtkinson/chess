package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLUserDAO implements UserDAO {
    private Connection connection;

    public SQLUserDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String query = "SELECT * FROM User WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new UserData(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("email")
                );
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error accessing user data", e);
        }
        return null;
    }

    @Override
    public boolean addUser(UserData user) throws DataAccessException {
        String query = "INSERT INTO User (username, password, email) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getEmail());
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error adding user data", e);
        }
    }

    @Override
    public boolean deleteUser(String username) throws DataAccessException {
        String query = "DELETE FROM User WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting user data", e);
        }
    }

    @Override
    public boolean updateUser(UserData user) throws DataAccessException {
        String query = "UPDATE User SET password = ?, email = ? WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getPassword());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getUsername());
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating user data", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String query = "TRUNCATE TABLE User";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing user data", e);
        }
    }

    @Override
    public boolean usernameExists(String username) throws DataAccessException {
        String query = "SELECT * FROM User WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new DataAccessException("Error checking username existence", e);
        }
    }

    @Override
    public boolean verifyUser(UserData user) throws DataAccessException {
        String query = "SELECT * FROM User WHERE username = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new DataAccessException("Error verifying user data", e);
        }
    }

    @Override
    public List<UserData> getAllUsers() throws DataAccessException {
        List<UserData> userList = new ArrayList<>();
        String query = "SELECT * FROM User";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                userList.add(new UserData(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("email")
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving all users", e);
        }
        return userList;
    }

    // Extra create the User table if it DON't exist
    public void createTable() throws DataAccessException {
        String query = """
            CREATE TABLE IF NOT EXISTS User (
                username VARCHAR(255) NOT NULL PRIMARY KEY,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
        """;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating User table", e);
        }
    }
}
