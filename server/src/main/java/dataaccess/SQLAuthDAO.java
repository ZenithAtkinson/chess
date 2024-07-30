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
    private static final String INSERT_AUTH_SQL = "INSERT INTO auth_tokens (token, username) VALUES (?, ?)";
    private static final String SELECT_AUTH_SQL = "SELECT * FROM auth_tokens WHERE token = ?";
    private static final String DELETE_AUTH_SQL = "DELETE FROM auth_tokens WHERE token = ?";
    private static final String DELETE_ALL_AUTH_SQL = "DELETE FROM auth_tokens";
    private static final String SELECT_ALL_AUTH_SQL = "SELECT * FROM auth_tokens";

    @Override
    public AuthData getAuthData(String token) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_AUTH_SQL)) {
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(rs.getString("token"), rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding auth data", e);
        }
        return null;
    }

    @Override
    public boolean addAuthData(AuthData authData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_AUTH_SQL)) {
            stmt.setString(1, authData.getAuthToken());
            stmt.setString(2, authData.getUsername());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error adding auth data", e);
        }
    }

    @Override
    public boolean deleteAuthData(String token) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_AUTH_SQL)) {
            stmt.setString(1, token);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth data", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_ALL_AUTH_SQL)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing auth data", e);
        }
    }

    @Override
    public List<AuthData> getAllAuthData() throws DataAccessException {
        List<AuthData> authTokens = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_AUTH_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                authTokens.add(new AuthData(rs.getString("token"), rs.getString("username")));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving all auth data", e);
        }
        return authTokens;
    }

    @Override
    public String generateAuthToken(String username) throws DataAccessException {
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, username);
        addAuthData(authData);
        return token;
    }
}
