package dataaccess;

import model.GameData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLGameDAO implements GameDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLGameDAO.class);
    private int nextId = 1;

    public SQLGameDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            LOGGER.error("Error configuring database: {}", e.getMessage());
        }
    }

    private void configureDatabase() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS Games (" +
                    "gameID INT PRIMARY KEY AUTO_INCREMENT, " +
                    "whiteUsername VARCHAR(255) DEFAULT NULL, " +
                    "blackUsername VARCHAR(255) DEFAULT NULL, " +
                    "gameName VARCHAR(255) NOT NULL, " +
                    "additionalParameter VARCHAR(255) NOT NULL)";
            stmt.executeUpdate(createTableSQL);
            setNextId();
        } catch (SQLException e) {
            LOGGER.error("Error configuring database: {}", e.getMessage());
            throw new DataAccessException("Error encountered while configuring the database");
        }
    }

    private void setNextId() throws DataAccessException {
        String sql = "SELECT MAX(gameID) AS maxID FROM Games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                nextId = rs.getInt("maxID") + 1;
            }
        } catch (SQLException e) {
            LOGGER.error("Error setting next ID: {}", e.getMessage());
            throw new DataAccessException("Error encountered while setting next ID");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = null;
        String sql = "SELECT * FROM Games WHERE gameID = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    game = new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            rs.getString("additionalParameter")
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error finding game with ID {}: {}", gameID, e.getMessage());
            throw new DataAccessException("Error encountered while finding game");
        }

        return game;
    }

    @Override
    public boolean addGame(GameData game) throws DataAccessException {
        game.setGameID(nextId++);
        if (game.getWhiteUsername() == null) game.setWhiteUsername("");
        if (game.getBlackUsername() == null) game.setBlackUsername("");
        if (game.getGameName() == null) game.setGameName("");
        if (game.getAdditionalParameter() == null) game.setAdditionalParameter("");

        String sql = "INSERT INTO Games (gameID, whiteUsername, blackUsername, gameName, additionalParameter) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, game.getGameID());
            stmt.setString(2, game.getWhiteUsername());
            stmt.setString(3, game.getBlackUsername());
            stmt.setString(4, game.getGameName());
            stmt.setString(5, game.getAdditionalParameter());

            stmt.executeUpdate();
            LOGGER.debug("Added game: {}", game);
            return true;
        } catch (SQLException e) {
            LOGGER.error("Error adding game: {}", e.getMessage());
            throw new DataAccessException("Error encountered while inserting game into the database");
        }
    }

    @Override
    public boolean updateGame(GameData game) throws DataAccessException {
        if (game.getWhiteUsername() == null) game.setWhiteUsername("");
        if (game.getBlackUsername() == null) game.setBlackUsername("");
        if (game.getGameName() == null) game.setGameName("");
        if (game.getAdditionalParameter() == null) game.setAdditionalParameter("");

        String sql = "UPDATE Games SET whiteUsername = ?, blackUsername = ?, gameName = ?, additionalParameter = ? WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, game.getWhiteUsername());
            stmt.setString(2, game.getBlackUsername());
            stmt.setString(3, game.getGameName());
            stmt.setString(4, game.getAdditionalParameter());
            stmt.setInt(5, game.getGameID());

            stmt.executeUpdate();
            LOGGER.debug("Updated game: {}", game);
            return true;
        } catch (SQLException e) {
            LOGGER.error("Error updating game: {}", e.getMessage());
            throw new DataAccessException("Error encountered while updating game in the database");
        }
    }

    @Override
    public boolean deleteGame(int gameID) throws DataAccessException {
        String sql = "DELETE FROM Games WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);

            int rowsAffected = stmt.executeUpdate();
            LOGGER.debug("Deleted game with ID {}: {} rows affected", gameID, rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Error deleting game with ID {}: {}", gameID, e.getMessage());
            throw new DataAccessException("Error encountered while deleting game from the database");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
            nextId = 1;
            LOGGER.debug("Cleared all games");
        } catch (SQLException e) {
            LOGGER.error("Error clearing games: {}", e.getMessage());
            throw new DataAccessException("Error encountered while clearing games from the database");
        }
    }

    @Override
    public Collection<GameData> getAllGames() throws DataAccessException {
        Collection<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM Games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                GameData game = new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        rs.getString("additionalParameter")
                );
                games.add(game);
            }
            LOGGER.debug("Retrieved all games: {}", games.size());
        } catch (SQLException e) {
            LOGGER.error("Error getting all games: {}", e.getMessage());
            throw new DataAccessException("Error encountered while getting all games from the database");
        }
        return games;
    }
}
