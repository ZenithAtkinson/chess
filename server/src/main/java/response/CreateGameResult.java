package response;

public class CreateGameResult {
    private Integer gameID;
    private String message;

    public CreateGameResult(int gameID) {
        this.gameID = gameID;
    }

    public CreateGameResult(String message) {
        this.message = message;
        this.gameID = null;
    }

    public Integer getGameID() {
        return gameID;
    }

    public String getMessage() {
        return message;
    }
}
