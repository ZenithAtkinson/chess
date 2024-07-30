package dataaccess;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DatabaseInitializationTest {

    @Test
    public void testDatabaseInitialization() {
        try {
            DatabaseManager.initializeDatabase();
            Assertions.assertTrue(true);
        } catch (DataAccessException e) {
            Assertions.fail("Failed to initialize the database and tables");
        }
    }
}
