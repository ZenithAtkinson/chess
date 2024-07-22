package dataaccess;

import model.GameData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int currentGameID = 1;

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public boolean addGame(GameData game) {
        game.setGameID(currentGameID++);
        games.put(game.getGameID(), game);
        return true;
    }

    @Override
    public boolean updateGame(GameData game) {
        if (games.containsKey(game.getGameID())) {
            games.put(game.getGameID(), game);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteGame(int gameID) {
        return games.remove(gameID) != null;
    }

    @Override
    public void clear() {
        games.clear();
        currentGameID = 1;
    }

    @Override
    public Collection<GameData> getAllGames() {
        return games.values();
    }
}
