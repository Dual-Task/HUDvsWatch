package edu.gatech.ic.hudvswatch.utils;

public class Assert {
    public static void that(boolean condition) {
        if (!condition) {
            throw new AssertionError("Condition failed");
        }
    }

    public static void fail() {
        throw new AssertionError("Assertion failed");
    }
}
