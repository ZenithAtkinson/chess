package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLUserDAO implements UserDAO {

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding user", e);
        }
        return null;
    }

    @Override
    public boolean addUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting new user", e);
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
                users.add(new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email")));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving all users", e);
        }
        return users;
    }
}
