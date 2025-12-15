package com.course.lab02.sealed;

public class AuthService {

    // LEGACY: Since the interface is Open, the compiler forces a 'default' case.
    public String checkLegacy(LegacyAuth auth) {
        return switch (auth) {
            case LegacyAuth.Password p -> "Legacy Password";
            case LegacyAuth.FaceID f   -> "Legacy FaceID";
            case LegacyAuth.HackerLogin h -> "Hacker Detected";
            // We MUST handle 'default' because unknown classes might exist.
            case null, default -> "Unknown"; 
        };
    }

    /*
     * TODO: Implement Switch for SealedAuth
     */
    public String checkSealed(SealedAuth auth) {
        // <--- PASTE/TYPE SWITCH HERE
        return "";
    }
}