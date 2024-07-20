package dataaccess;

import model.GameData;
import java.util.List;

public interface GameDAO {
    GameData getGame(int gameID) throws DataAccessException;
    boolean addGame(GameData game) throws DataAccessException;
    boolean updateGame(GameData game) throws DataAccessException;
    boolean deleteGame(int gameID) throws DataAccessException;
    void clear() throws DataAccessException;
    List<GameData> getAllGames() throws DataAccessException;
}
