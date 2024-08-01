package dataaccess;

import model.AuthData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {
    private final Map<String, AuthData> memoryAuthData = new HashMap<>();

    public SQLAuthDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            System.err.println("Error configuring database: " + e.getMessage());
        }
    }

    private void configureDatabase() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS AuthTokens (" +
                    "authToken VARCHAR(255) PRIMARY KEY, " +
                    "username VARCHAR(255) NOT NULL)";
            stmt.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while configuring the database");
        }
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        AuthData authData = null;
        String sql = "SELECT * FROM AuthTokens WHERE authToken = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    authData = new AuthData(
                            rs.getString("authToken"),
                            rs.getString("username")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Falling back to in-memory storage: " + e.getMessage());
            authData = memoryAuthData.get(authToken);
        }

        return authData;
    }

    @Override
    public boolean addAuthData(AuthData authData) throws DataAccessException {
        String sql = "INSERT INTO AuthTokens (authToken, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authData.getAuthToken());
            stmt.setString(2, authData.getUsername());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Falling back to in-memory storage: " + e.getMessage());
            memoryAuthData.put(authData.getAuthToken(), authData);
            return true;
        }
    }

    @Override
    public boolean deleteAuthData(String authToken) throws DataAccessException {
        String sql = "DELETE FROM AuthTokens WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Falling back to in-memory storage: " + e.getMessage());
            memoryAuthData.remove(authToken);
            return true;
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM AuthTokens";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Falling back to in-memory storage: " + e.getMessage());
            memoryAuthData.clear();
        }
    }

    @Override
    public Collection<AuthData> getAllAuthData() throws DataAccessException {
        Collection<AuthData> authDataList = new ArrayList<>();
        String sql = "SELECT * FROM AuthTokens";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                AuthData authData = new AuthData(
                        rs.getString("authToken"),
                        rs.getString("username")
                );
                authDataList.add(authData);
            }
        } catch (SQLException e) {
            System.err.println("Falling back to in-memory storage: " + e.getMessage());
            authDataList.addAll(memoryAuthData.values());
        }
        return authDataList;
    }

    @Override
    public String generateAuthToken(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        addAuthData(authData);
        return authToken;
    }
}
