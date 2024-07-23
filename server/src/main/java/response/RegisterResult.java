package response;

public class RegisterResult {
    private String username;
    private String authToken;
    private String message;
    private boolean success;

    public RegisterResult(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
        this.success = true;
    }

    public RegisterResult(String message) {
        this.message = message;
        this.success = false;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
}
