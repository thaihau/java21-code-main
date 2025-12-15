package com.course.lab02.sealed;

// PROBLEM: This interface is "Open".
// Any class, anywhere in the project, can implement it.
public interface LegacyAuth {

    // 1. Valid Implementation: Password
    record Password(String hash) implements LegacyAuth {}

    // 2. Valid Implementation: FaceID
    record FaceID(byte[] data) implements LegacyAuth {}

    // 3. THE VULNERABILITY
    // Because the interface is not sealed, we can create a rogue implementation.
    // In a real project, this class could be in a different package or library.
    class HackerLogin implements LegacyAuth {
        public String bypass() { return "I'm in!"; }
    }
}