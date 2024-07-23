package dataaccess;

import model.GameData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextId = 1;

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public boolean addGame(GameData game) {
        if (games.containsKey(game.getGameID())) {
            return false;
        }
        game.setGameID(nextId++);
        games.put(game.getGameID(), game);
        return true;
    }

    @Override
    public boolean updateGame(GameData game) {
        if (!games.containsKey(game.getGameID())) {
            return false;
        }
        games.put(game.getGameID(), game);
        return true;
    }

    @Override
    public boolean deleteGame(int gameID) {
        if (!games.containsKey(gameID)) {
            return false;
        }
        games.remove(gameID);
        return true;
    }

    @Override
    public void clear() {
        games.clear();
        nextId = 1;
    }

    @Override
    public Collection<GameData> getAllGames() {
        return games.values();
    }
}
