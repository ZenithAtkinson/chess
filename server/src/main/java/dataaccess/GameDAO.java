package dataaccess;

import model.GameData;
import java.util.Collection;

public interface GameDAO {
    // gets the GameData for a given gameId
    GameData getGame(int gameID) throws DataAccessException;
    // adds the given GameData
    boolean addGame(GameData game) throws DataAccessException;
    boolean updateGame(GameData game) throws DataAccessException;

    boolean deleteGame(int gameID) throws DataAccessException;

    // deletes the GameData for a given gameId
    //boolean deleteGame(int gameID) throws DataAccessException;
    void clear() throws DataAccessException;
    Collection<GameData> getAllGames() throws DataAccessException;
}
