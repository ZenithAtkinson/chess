package dataaccess;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionTest {

    @Test
    public void testConnection() {
        try (Connection connection = DatabaseManager.getConnection()) {
            Assertions.assertNotNull(connection);
        } catch (SQLException | DataAccessException e) {
            Assertions.fail("Failed to establish connection to the database");
        }
    }
}
