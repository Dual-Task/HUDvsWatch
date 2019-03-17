package edu.gatech.ic.hudvswatch.models;

import edu.gatech.ic.hudvswatch.utils.Assert;

public class VisualSearchTaskGrid {

    private int[][] grid;
    private int numberOfValues;
    private int targetNumber;

    public VisualSearchTaskGrid(int[][] grid, int numberOfValues) {
        this.grid = grid;
        this.numberOfValues = numberOfValues;

        Assert.that(this.getNumberOfValuesInGrid() == this.numberOfValues);
        Assert.that(!this.doesContainTargetNumber());
    }

    public VisualSearchTaskGrid(int[][] grid, int numberOfValues, int targetNumber) {
        this.grid = grid;
        this.numberOfValues = numberOfValues;
        this.targetNumber = targetNumber;

        Assert.that(this.getNumberOfValuesInGrid() == this.numberOfValues);
        Assert.that(this.doesContainTargetNumber());
    }

    public int[][] getGrid() {
        return grid;
    }

    int getTargetNumberValue() {
        Assert.that(this.targetNumber != 0);
        Assert.that(this.doesContainTargetNumber());
        return this.targetNumber;
    }

    public int getNumberOfValuesInGrid() {
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

    public boolean doesContainTargetNumber() {
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
