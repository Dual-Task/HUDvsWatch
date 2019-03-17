package edu.gatech.ic.hudvswatch.utils;

public class VisualSearchTaskGrid {

    private int[][] grid;
    private int numberOfValues;
    private int targetNumber;

    VisualSearchTaskGrid(int[][] grid, int numberOfValues) {
        this.grid = grid;
        this.numberOfValues = numberOfValues;

        Verify.that(this.getNumberOfValuesInGrid() == this.numberOfValues);
        Verify.that(!this.doesContainTargetNumber());
    }

    VisualSearchTaskGrid(int[][] grid, int numberOfValues, int targetNumber) {
        this.grid = grid;
        this.numberOfValues = numberOfValues;
        this.targetNumber = targetNumber;

        Verify.that(this.getNumberOfValuesInGrid() == this.numberOfValues);
        Verify.that(this.doesContainTargetNumber());
    }

    int[][] getGrid() {
        return grid;
    }

    int getTargetNumberValue() {
        Verify.that(this.targetNumber != 0);
        Verify.that(this.doesContainTargetNumber());
        return this.targetNumber;
    }

    int getNumberOfValuesInGrid() {
        int count = 0;

        for (int i = 0; i < getGrid().length; i++) {
            for (int j = 0; j < getGrid()[0].length; j++) {
                if (getGrid()[i][j] != 0) {
                    count++;
                }
            }
        }

        return count;
    }

    boolean doesContainTargetNumber() {
        // If the target number is not set, it cannot be included in the grid
        if (this.targetNumber == 0) {
            return false;
        }

        for (int i = 0; i < getGrid().length; i++) {
            for (int j = 0; j < getGrid()[0].length; j++) {
                if (getGrid()[i][j] == this.targetNumber) {
                    return true;
                }
            }
        }
        return false;
    }
}
