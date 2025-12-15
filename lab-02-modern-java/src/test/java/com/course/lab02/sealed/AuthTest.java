package com.course.lab02.sealed;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthTest {

    @Test
    void testSealedSecurity() {
        AuthService service = new AuthService();
        SealedAuth.Password p = new SealedAuth.Password("secret123");

        /*
         * TODO: Test SealedAuth logic
         */
         // <--- PASTE/TYPE CODE HERE
    }
}