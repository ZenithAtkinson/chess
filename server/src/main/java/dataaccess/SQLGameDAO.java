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
        String sql = "INSERT INTO games (gameID, whiteUsername, blackUsername, name, gameState, additionalParameter) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String gameStateJson = gson.toJson(game);
            String additionalParamJson = gson.toJson(gameData.getAdditionalParameter());

            stmt.setInt(1, gameData.getGameID());
            stmt.setString(2, gameData.getWhiteUsername());
            stmt.setString(3, gameData.getBlackUsername());
            stmt.setString(4, gameData.getGameName());
            stmt.setString(5, gameStateJson);
            stmt.setString(6, additionalParamJson);

            // Debug statements
            System.out.println("Executing SQL: " + sql);
            System.out.println("Parameters: ");
            System.out.println("gameID: " + gameData.getGameID());
            System.out.println("whiteUsername: " + gameData.getWhiteUsername());
            System.out.println("blackUsername: " + gameData.getBlackUsername());
            System.out.println("name: " + gameData.getGameName());
            System.out.println("gameState: " + gameStateJson);
            System.out.println("additionalParameter: " + additionalParamJson);

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("SQL Message: " + e.getMessage());
            throw new DataAccessException("Error adding game", e);
        }
    }


    public ChessGame getGame(int gameID) throws DataAccessException {
        String sql = "SELECT gameState FROM games WHERE gameID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return gson.fromJson(rs.getString("gameState"), ChessGame.class);
                } else {
                    throw new DataAccessException("Game not found");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game", e);
        }
    }

    public void updateGame(GameData gameData, ChessGame game) throws DataAccessException {
        String sql = "UPDATE games SET gameState = ?, whiteUsername = ?, blackUsername = ?, name = ?, additionalParameter = ? WHERE gameID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, gson.toJson(game));
            stmt.setString(2, gameData.getWhiteUsername());
            stmt.setString(3, gameData.getBlackUsername());
            stmt.setString(4, gameData.getGameName());
            stmt.setString(5, gson.toJson(gameData.getAdditionalParameter()));
            stmt.setInt(6, gameData.getGameID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game", e);
        }
    }

    public List<GameData> getAllGames() throws DataAccessException {
        List<GameData> allGames = new ArrayList<>();
        String sql = "SELECT gameID, whiteUsername, blackUsername, name, additionalParameter FROM games";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                GameData gameData = new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("name"),
                        gson.fromJson(rs.getString("additionalParameter"), Object.class)
                );
                allGames.add(gameData);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving all games", e);
        }
        return allGames;
    }

    public void clear() throws DataAccessException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM moves");
            stmt.executeUpdate("DELETE FROM games");
            stmt.executeUpdate("DELETE FROM users");  // Clear users table as well
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing tables", e);
        }
    }

}
