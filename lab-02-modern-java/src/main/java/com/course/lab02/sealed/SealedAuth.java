package com.course.lab02.sealed;

/*
 * TODO: Refactor to Sealed Interface
 */
public interface SealedAuth { 

    record Password(String hash) implements SealedAuth {}
    record FaceID(byte[] data) implements SealedAuth {}

    /*
     * TODO: Paste Experiment Here
     */
}