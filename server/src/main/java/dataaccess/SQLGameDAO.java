package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLGameDAO implements GameDAO {
    private Connection connection;

    public SQLGameDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try {
            String query = "SELECT * FROM GameData WHERE gameID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, gameID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new GameData(
                        resultSet.getInt("gameID"),
                        resultSet.getString("whiteUsername"),
                        resultSet.getString("blackUsername"),
                        resultSet.getString("gameName"),
                        (ChessGame) resultSet.getObject("game") // Assuming ChessGame is serializable
                );
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error accessing game data", e);
        }
        return null;
    }

    @Override
    public boolean addGame(GameData game) throws DataAccessException {
        try {
            String query = "INSERT INTO GameData (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, game.getGameID());
            statement.setString(2, game.getWhiteUsername());
            statement.setString(3, game.getBlackUsername());
            statement.setString(4, game.getGameName());
            statement.setObject(5, game.getGame()); // Assuming ChessGame is serializable
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error adding game data", e);
        }
    }

    @Override
    public boolean updateGame(GameData game) throws DataAccessException {
        try {
            String query = "UPDATE GameData SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, game.getWhiteUsername());
            statement.setString(2, game.getBlackUsername());
            statement.setString(3, game.getGameName());
            statement.setObject(4, game.getGame()); // Assuming ChessGame is serializable
            statement.setInt(5, game.getGameID());
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game data", e);
        }
    }

    @Override
    public boolean deleteGame(int gameID) throws DataAccessException {
        try {
            String query = "DELETE FROM GameData WHERE gameID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, gameID);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting game data", e);
        }
    }
}
