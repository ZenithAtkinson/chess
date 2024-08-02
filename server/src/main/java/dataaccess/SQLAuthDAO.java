package dataaccess;

import model.AuthData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLAuthDAO implements AuthDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLAuthDAO.class);

    public SQLAuthDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            LOGGER.error("Error configuring database: {}", e.getMessage());
        }
    }

    //WHY
    private void configureDatabase() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS auth (" +
                    "authToken VARCHAR(255) PRIMARY KEY, " +
                    "username VARCHAR(255) NOT NULL)";
            stmt.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            LOGGER.error("Error configuring database: {}", e.getMessage());
            throw new DataAccessException("Error encountered while configuring the database");
        }
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        AuthData authData = null;
        String sql = "SELECT * FROM auth WHERE authToken = ?;";

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
            LOGGER.error("Error finding auth token {}: {}", authToken, e.getMessage());
            throw new DataAccessException("Error encountered while finding auth token");
        }

        return authData;
    }

    @Override
    public boolean addAuthData(AuthData authData) throws DataAccessException {
        String sql = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authData.getAuthToken());
            stmt.setString(2, authData.getUsername());
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                authData.setAuthToken(UUID.randomUUID().toString());
                return addAuthData(authData);
            } else {
                throw new DataAccessException("Error adding auth data", e);
            }
        }
    }

    @Override
    public boolean deleteAuthData(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Deleting auth data failed, no rows affected.");
            }

            LOGGER.debug("Deleted auth data with token {}: {} rows affected", authToken, affectedRows);
            return true;
        } catch (SQLException e) {
            LOGGER.error("Error deleting auth data with token {}: {}", authToken, e.getMessage());
            throw new DataAccessException("Error encountered while deleting auth data from the database");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM auth";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
            LOGGER.debug("Cleared all auth data");
        } catch (SQLException e) {
            LOGGER.error("Error clearing auth data: {}", e.getMessage());
            throw new DataAccessException("Error encountered while clearing auth data from the database");
        }
    }

    @Override
    public Collection<AuthData> getAllAuthData() throws DataAccessException {
        Collection<AuthData> authDataList = new ArrayList<>();
        String sql = "SELECT * FROM auth";
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
            LOGGER.debug("Retrieved all auth data: {}", authDataList.size());
        } catch (SQLException e) {
            LOGGER.error("Error getting all auth data: {}", e.getMessage());
            throw new DataAccessException("Error encountered while getting all auth data from the database");
        }
        return authDataList;
    }

    @Override
    public String generateAuthToken(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        addAuthData(authData);
        LOGGER.debug("Generated auth token for username {}: {}", username, authToken);
        return authToken;
    }
}
