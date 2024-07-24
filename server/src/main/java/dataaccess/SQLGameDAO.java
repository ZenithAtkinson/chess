package dataaccess;

import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {
    private final Connection conn;

    public SQLGameDAO(Connection conn) {
        this.conn = conn;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM Games WHERE gameID = ?;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                game = new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        null // Assuming additional parameter is not needed from database
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding game");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return game;
    }

    @Override
    public boolean addGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO Games (whiteUsername, blackUsername, gameName, additionalParameter) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, game.getWhiteUsername());
            stmt.setString(2, game.getBlackUsername());
            stmt.setString(3, game.getGameName());
            //stmt.setObject(4, game.getAdditionalParameter()); /djust based on actual type
            ////not implemented

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting game into the database");
        }
    }

    @Override
    public boolean updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE Games SET whiteUsername = ?, blackUsername = ?, gameName = ?, additionalParameter = ? WHERE gameID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, game.getWhiteUsername());
            stmt.setString(2, game.getBlackUsername());
            stmt.setString(3, game.getGameName());
            //stmt.setObject(4, game.getAdditionalParameter()); //Adjust
            //not implemented
            stmt.setInt(5, game.getGameID());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while updating game in the database");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Games";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing games from the database");
        }
    }

    @Override
    public Collection<GameData> getAllGames() throws DataAccessException {
        Collection<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM Games";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                GameData game = new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        null // Assuming additional parameter is not needed from database
                );
                games.add(game);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while getting all games from the database");
        }
        return games;
    }
}
