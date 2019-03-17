package edu.gatech.ic.hudvswatch.utils;

public class Verify {
    public static void that(boolean condition) {
        if (!condition) {
            throw new AssertionError("Condition failed");
        }
    }
}
