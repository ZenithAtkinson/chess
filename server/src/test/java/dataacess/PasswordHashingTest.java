package dataacess;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHashingTest {

    @Test
    void testHashPassword() {
        String plainPassword = "mySecurePassword";
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        assertNotNull(hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$"));
        assertNotEquals(plainPassword, hashedPassword);
    }

    @Test
    void testVerifyPassword() {
        String plainPassword = "mySecurePassword";
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        // Positive case: correct password
        assertTrue(BCrypt.checkpw(plainPassword, hashedPassword));

        // Negative case: incorrect password
        assertFalse(BCrypt.checkpw("wrongPassword", hashedPassword));
    }
}
