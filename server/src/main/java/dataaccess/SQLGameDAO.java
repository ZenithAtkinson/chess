package dataaccess;

import model.GameData;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {
    private static final String CREATE_TABLE_STATEMENT = getCreateStatement();

    private static String getCreateStatement() { // Every time a change is made, REMEMBER: drop the table again
        return """
            CREATE TABLE IF NOT EXISTS `game` (
                `gameID` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                `gameName` VARCHAR(256) NOT NULL,
                `whiteUsername` VARCHAR(64),
                `blackUsername` VARCHAR(64),
                `additionalParameter` VARCHAR(255) DEFAULT 'none' 
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """;
    }

    public SQLGameDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            System.out.println("Database unable to be configured: " + e.getMessage());
        }
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            var statements = new String[]{CREATE_TABLE_STATEMENT};
            for (var statement : statements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    @Override
    public boolean addGame(GameData gameData) throws DataAccessException {
        String sql = "INSERT INTO game (whiteUsername, blackUsername, gameName, additionalParameter) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, gameData.getWhiteUsername() != null ? gameData.getWhiteUsername() : null);
            stmt.setString(2, gameData.getBlackUsername() != null ? gameData.getBlackUsername() : null);
            stmt.setString(3, gameData.getGameName());
            stmt.setString(4, gameData.getAdditionalParameter() != null ? gameData.getAdditionalParameter() : "none");
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Adding game failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    gameData.setGameID(generatedKeys.getInt(1));
                } else {
                    throw new DataAccessException("Adding game failed, no ID obtained.");
                }
            }
            return true;
        } catch (SQLException e) {
            throw new DataAccessException("Error adding game", e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM game WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            rs.getString("additionalParameter")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting game", e);
        }
        return null;
    }

    @Override
    public boolean updateGame(GameData gameData) throws DataAccessException {
        String sql = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, additionalParameter = ? WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, gameData.getWhiteUsername());
            stmt.setString(2, gameData.getBlackUsername());
            stmt.setString(3, gameData.getGameName());
            stmt.setString(4, gameData.getAdditionalParameter());
            stmt.setInt(5, gameData.getGameID());
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game", e);
        }
    }

    @Override
    public boolean deleteGame(int gameID) throws DataAccessException {
        String sql = "DELETE FROM game WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting game", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM game";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing games", e);
        }
    }

    @Override
    public Collection<GameData> getAllGames() throws DataAccessException {
        Collection<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM game";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                games.add(new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        rs.getString("additionalParameter")
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting all games", e);
        }
        return games;
    }
}
