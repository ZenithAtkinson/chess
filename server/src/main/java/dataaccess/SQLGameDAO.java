package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLGameDAO {
    private final Connection conn;
    private final Gson gson;

    public SQLGameDAO(Connection conn) {
        this.conn = conn;
        this.gson = new Gson();
    }

    public void addGame(GameData gameData, ChessGame game) throws DataAccessException {
        String sql = "INSERT INTO games (gameID, gameData) VALUES (?, ?);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String gameJson = gson.toJson(game);
            stmt.setInt(1, gameData.getGameID());
            stmt.setString(2, gameJson);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error adding game", e);
        }
    }

    public ChessGame getGame(int gameID) throws DataAccessException {
        String sql = "SELECT gameData FROM games WHERE gameID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String gameJson = rs.getString("gameData");
                return gson.fromJson(gameJson, ChessGame.class);
            } else {
                throw new DataAccessException("Game not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding game", e);
        }
    }

    public void updateGame(GameData gameData, ChessGame game) throws DataAccessException {
        String sql = "UPDATE games SET gameData = ? WHERE gameID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String gameJson = gson.toJson(game);
            stmt.setString(1, gameJson);
            stmt.setInt(2, gameData.getGameID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game", e);
        }
    }

    public List<GameData> getAllGames() throws DataAccessException {
        String sql = "SELECT gameID, gameData FROM games;";
        List<GameData> games = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int gameID = rs.getInt("gameID");
                String gameJson = rs.getString("gameData");
                ChessGame game = gson.fromJson(gameJson, ChessGame.class);
                GameData gameData = new GameData(gameID, null, null, null, game);
                games.add(gameData);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting all games", e);
        }
        return games;
    }

    public void clear() throws DataAccessException {
        String sql = "DELETE FROM games;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing games", e);
        }
    }
}
