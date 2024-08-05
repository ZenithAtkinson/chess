package ServerUtils;

public class ResponseException extends Exception {
    //Implemented because the PetShop code does it
    final private int statusCode;

    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int StatusCode() {
        return statusCode;
    }
}