package dataaccess;

import model.GameData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private Map<Integer, GameData> games = new HashMap<>();

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public boolean addGame(GameData game) throws DataAccessException {
        if (games.containsKey(game.getGameID())) {
            return false;
        }
        games.put(game.getGameID(), game);
        return true;
    }

    @Override
    public boolean updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.getGameID())) {
            return false;
        }
        games.put(game.getGameID(), game);
        return true;
    }

    @Override
    public boolean deleteGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            return false;
        }
        games.remove(gameID);
        return true;
    }

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }

    @Override
    public List<GameData> getAllGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }
}
