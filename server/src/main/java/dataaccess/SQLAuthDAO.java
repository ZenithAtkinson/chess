package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        String sql = "SELECT * FROM auth_tokens WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(rs.getString("token"), rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding auth token", e);
        }
        return null;
    }

    @Override
    public boolean addAuthData(AuthData authData) throws DataAccessException {
        String sql = "INSERT INTO auth_tokens (token, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authData.getAuthToken());
            stmt.setString(2, authData.getUsername());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting new auth token", e);
        }
    }

    @Override
    public boolean deleteAuthData(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth_tokens WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth token", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM auth_tokens";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing auth tokens", e);
        }
    }

    @Override
    public List<AuthData> getAllAuthData() throws DataAccessException {
        List<AuthData> tokens = new ArrayList<>();
        String sql = "SELECT * FROM auth_tokens";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tokens.add(new AuthData(rs.getString("token"), rs.getString("username")));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving all auth tokens", e);
        }
        return tokens;
    }

    @Override
    public String generateAuthToken(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        if (addAuthData(authData)) {
            return authToken;
        } else {
            throw new DataAccessException("Error generating new auth token");
        }
    }
}
