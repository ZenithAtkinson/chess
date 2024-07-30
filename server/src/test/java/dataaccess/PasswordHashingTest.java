package dataaccess;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordHashingTest {

    @Test
    public void testPasswordHashing() {
        String password = "testPassword";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        Assertions.assertTrue(BCrypt.checkpw(password, hashedPassword));
    }
}
