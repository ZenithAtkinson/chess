
/**
 * Indicates there was an error connecting to the database
 */
package dataaccess;

public class DataAccessException extends Exception {
    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
