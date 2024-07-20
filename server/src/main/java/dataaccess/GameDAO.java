package dataaccess;

import model.GameData;

public interface GameDAO {
    GameData getGame(int gameID) throws DataAccessException;
    boolean addGame(GameData game) throws DataAccessException;
    boolean updateGame(GameData game) throws DataAccessException;
    boolean deleteGame(int gameID) throws DataAccessException;
}
