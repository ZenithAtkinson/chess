package websocket.messages;

import chess.ChessGame;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
//Needs to implement 3 main server messages: Load game, error, and notificatoin (see specs for details)
public class ServerMessage {
    ServerMessageType serverMessageType;
    private ChessGame game;
    private String message;
    private String errorMessage;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    // Constructor for LOAD_GAME messages
    public ServerMessage(ChessGame game) {
        this.serverMessageType = ServerMessageType.LOAD_GAME;
        this.game = game;
    }

    public ServerMessageType getServerMessageType() {
        return serverMessageType;
    }

    public ChessGame getGame() {
        return game;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    //error and notifications
    public ServerMessage(ServerMessageType valtype, String message) {
        this.serverMessageType = valtype;
        switch (valtype) {
            case NOTIFICATION -> this.message = message;
            case ERROR -> this.errorMessage = message;
            default -> throw new IllegalArgumentException("Incorrect variable type");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ServerMessage that))
            return false;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }


}
