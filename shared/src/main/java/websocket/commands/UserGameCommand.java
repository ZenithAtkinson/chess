package websocket.commands;

import chess.ChessMove;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */

/**
 * Root Client sends CONNECT
 * Server sends a LOAD_GAME message back to the root client.
 * Server sends a Notification message to all other clients in that game informing them the root client connected to the game, either as a player (in which case their color must be specified) or as an observer.
 * Root Client sends MAKE_MOVE:
 * Server verifies the validity of the move.
 * Game is updated to represent the move. Game is updated in the database.
 * Server sends a LOAD_GAME message to all clients in the game (including the root client) with an updated game.
 * Server sends a Notification message to all other clients in that game informing them what move was made.
 * If the move results in check, checkmate or stalemate the server sends a Notification message to all clients.
 * Root Client sends LEAVE:
 * If a player is leaving, then the game is updated to remove the root client. Game is updated in the database.
 * Server sends a Notification message to all other clients in that game informing them that the root client left. This applies to both players and observers.
 * Root Client sends RESIGN:
 * Server marks the game as over (no more moves can be made). Game is updated in the database.
 * Server sends a Notification message to all clients in that game informing them that the root client resigned. This applies to both players and observers.
 */
public class UserGameCommand {

    private final CommandType commandType;

    private final String authToken;

    private final Integer gameID;

    private final ChessMove move; //added

    private final String username; //Added username field

    private boolean active; //added

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move, String username) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.move = move;
        this.username = username;
        this.active = active;
    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getGameID() {
        return gameID;
    }

    public ChessMove getMove() {
        return move;
    }

    //getters and setters for the active field
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand that)) {
            return false;
        }
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID());
    }
}
