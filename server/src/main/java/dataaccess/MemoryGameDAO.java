package dataaccess;

import model.GameData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextId = 1;

    @Override
    //Get game by  ID
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    // Add a new game/memory
    public boolean addGame(GameData game) {
        if (games.containsKey(game.getGameID())) {
            return false;
        }
        game.setGameID(nextId++);
        games.put(game.getGameID(), game);
        return true;
    }

    @Override
    // Update existing game?
    public boolean updateGame(GameData game) {
        if (!games.containsKey(game.getGameID())) {
            return false;
        }
        games.put(game.getGameID(), game);
        return true;
    }

    @Override
    // Delete a game by its ID
    public boolean deleteGame(int gameID) {
        if (!games.containsKey(gameID)) {
            return false;
        }
        games.remove(gameID);
        return true;
    }

    @Override
    // Clear all games from memory
    public void clear() {
        games.clear();
        nextId = 1;
    }

    @Override
    // GET all games
    public Collection<GameData> getAllGames() {
        return games.values();
    }
}
