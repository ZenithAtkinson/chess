package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {

    private final Gson gson;

    public SQLGameDAO() {
        this.gson = new Gson();
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM games WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new GameData(
                            rs.getInt("id"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("name"),
                            rs.getString("additionalParameter")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding game", e);
        }
        return null;
    }

    @Override
    public ChessGame getGameState(int gameID) throws DataAccessException {
        String sql = "SELECT gameState FROM games WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return gson.fromJson(rs.getString("gameState"), ChessGame.class);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding game state", e);
        }
        return null;
    }

    @Override
    public boolean addGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO games (id, whiteUsername, blackUsername, name, additionalParameter) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, game.getGameID());
            stmt.setString(2, game.getWhiteUsername());
            stmt.setString(3, game.getBlackUsername());
            stmt.setString(4, game.getGameName());
            stmt.setString(5, game.getAdditionalParameter().toString());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting new game", e);
        }
    }

    @Override
    public boolean addGameState(int gameID, ChessGame gameState) throws DataAccessException {
        // Ensure game exists
        if (getGame(gameID) == null) {
            // Insert a new game with default values
            String insertSql = "INSERT INTO games (id, whiteUsername, blackUsername, name, additionalParameter) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, gameID);
                insertStmt.setString(2, "defaultWhite");
                insertStmt.setString(3, "defaultBlack");
                insertStmt.setString(4, "defaultName");
                insertStmt.setString(5, "defaultParam");
                insertStmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DataAccessException("Error inserting default game", e);
            }
        }

        // Update the game state
        String sql = "UPDATE games SET gameState = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String gameStateJson = gson.toJson(gameState);
            System.out.println("Executing SQL: " + sql);
            System.out.println("With parameters: gameID = " + gameID + ", gameState = " + gameStateJson);

            stmt.setString(1, gameStateJson);
            stmt.setInt(2, gameID);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error updating game state", e);
        }
    }



    @Override
    public boolean updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE games SET whiteUsername = ?, blackUsername = ?, name = ?, additionalParameter = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, game.getWhiteUsername());
            stmt.setString(2, game.getBlackUsername());
            stmt.setString(3, game.getGameName());
            stmt.setString(4, game.getAdditionalParameter().toString());
            stmt.setInt(5, game.getGameID());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("Error updating game: Game not found");
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game", e);
        }
    }

    @Override
    public boolean updateGameState(int gameID, ChessGame gameState) throws DataAccessException {
        String sql = "UPDATE games SET gameState = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, gson.toJson(gameState));
            stmt.setInt(2, gameID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game state", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM games";
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
        String sql = "SELECT * FROM games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                games.add(new GameData(
                        rs.getInt("id"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("name"),
                        rs.getString("additionalParameter")
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving all games", e);
        }
        return games;
    }
}
