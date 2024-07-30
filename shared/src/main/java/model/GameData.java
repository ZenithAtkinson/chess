package model;

public class GameData {
    private int gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    private Object additionalParameter; // Replace with actual type if necessary

    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, Object additionalParameter) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.additionalParameter = additionalParameter;
    }

    // Getters and setters
    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Object getAdditionalParameter() {
        return additionalParameter;
    }

    public void setAdditionalParameter(Object additionalParameter) {
        this.additionalParameter = additionalParameter;
    }
}
