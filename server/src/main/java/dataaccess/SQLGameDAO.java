package dataaccess;

import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    private final Connection conn;

    public SQLGameDAO(Connection conn) {
        this.conn = conn;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM games WHERE gameID = ?;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                game = new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        null // Assuming ChessGame object needs to be constructed from another method
                );
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while finding game", e);
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
        String sql = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName) VALUES (?, ?, ?, ?);";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, game.getGameID());
            stmt.setString(2, game.getWhiteUsername());
            stmt.setString(3, game.getBlackUsername());
            stmt.setString(4, game.getGameName());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting game", e);
        }
    }

    @Override
    public boolean updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ? WHERE gameID = ?;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, game.getWhiteUsername());
            stmt.setString(2, game.getBlackUsername());
            stmt.setString(3, game.getGameName());
            stmt.setInt(4, game.getGameID());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while updating game", e);
        }
    }

    @Override
    public boolean deleteGame(int gameID) throws DataAccessException {
        String sql = "DELETE FROM games WHERE gameID = ?;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while deleting game", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM games;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while clearing games", e);
        }
    }

    @Override
    public List<GameData> getAllGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        ResultSet rs = null;
        String sql = "SELECT * FROM games;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            rs = stmt.executeQuery();
            while (rs.next()) {
                GameData game = new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        null // Assuming ChessGame object needs to be constructed from another method
                );
                games.add(game);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while retrieving all games", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return games;
    }
}
