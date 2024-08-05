package response;

public class CreateGameResult {
    private Integer gameID;
    private String message;

    public CreateGameResult(int gameID) {
        this.gameID = gameID;
    }
    //Constructor for failed game creation result
    public CreateGameResult(String message) {
        this.message = message;
        this.gameID = null;
    }

    //GameID
    public Integer getGameID() {
        return gameID;
    }

    //Main message
    public String getMessage() {
        return message;
    }
}
