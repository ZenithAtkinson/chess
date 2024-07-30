package dataaccess;

import model.GameData;
import chess.ChessGame;
import java.util.Collection;

public interface GameDAO {
    GameData getGame(int gameID) throws DataAccessException;
    boolean addGame(GameData game) throws DataAccessException;
    boolean updateGame(GameData game) throws DataAccessException;
    void clear() throws DataAccessException;
    Collection<GameData> getAllGames() throws DataAccessException;

    // Add the following methods
    boolean addGameState(int gameID, ChessGame gameState) throws DataAccessException;
    ChessGame getGameState(int gameID) throws DataAccessException;
    boolean updateGameState(int gameID, ChessGame gameState) throws DataAccessException;
}
