package request;

public class JoinGameRequest {
    private final int gameID;
    private final String playerColor;

    public JoinGameRequest(int gameID, String playerColor) {
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    //Get game ID
    public int getGameID() {
        return gameID;
    }

    //Get player color
    public String getPlayerColor() {
        return playerColor;
    }
}
