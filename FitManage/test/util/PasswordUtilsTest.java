package util;

import org.junit.Test;

import static org.junit.Assert.*;

public class PasswordUtilsTest {

    @Test
    public void hashYCheckPassword_funcionan() {
        String hash = PasswordUtils.hashPassword("secret");
        assertNotNull(hash);
        assertTrue(PasswordUtils.checkPassword("secret", hash));
        assertFalse(PasswordUtils.checkPassword("wrong", hash));
    }
}
