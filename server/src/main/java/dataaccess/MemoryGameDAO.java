package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private final Map<Integer, ChessGame> gameStates = new HashMap<>(); // New map for game states
    private int nextId = 1;

    @Override
    // Get game by ID
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
    // Update existing game
    public boolean updateGame(GameData game) {
        if (!games.containsKey(game.getGameID())) {
            return false;
        }
        games.put(game.getGameID(), game);
        return true;
    }

    @Override
    // Clear all games from memory
    public void clear() {
        games.clear();
        gameStates.clear(); // Clear game states
        nextId = 1;
    }

    @Override
    // Get all games
    public Collection<GameData> getAllGames() {
        return games.values();
    }

    // Add a new game state
    @Override
    public boolean addGameState(int gameID, ChessGame gameState) {
        if (gameStates.containsKey(gameID)) {
            return false;
        }
        gameStates.put(gameID, gameState);
        return true;
    }

    // Get a game state by ID
    @Override
    public ChessGame getGameState(int gameID) {
        return gameStates.get(gameID);
    }

    // Update an existing game state
    @Override
    public boolean updateGameState(int gameID, ChessGame gameState) {
        if (!gameStates.containsKey(gameID)) {
            return false;
        }
        gameStates.put(gameID, gameState);
        return true;
    }
}
