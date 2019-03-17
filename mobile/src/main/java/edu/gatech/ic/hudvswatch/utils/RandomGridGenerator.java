package edu.gatech.ic.hudvswatch.utils;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.gatech.ic.hudvswatch.models.VisualSearchTaskGrid;

/**
 * Created by p13i on 10/29/18.
 */

public class RandomGridGenerator {
    private static final RandomGridGenerator sInstance = new RandomGridGenerator();
    public static RandomGridGenerator getInstance() {
        return sInstance;
    }

    private static Random sRandom = new Random(42);

    private static final int GRID_HEIGHT = 10;
    private static final int GRID_WIDTH = 10;
    private static final int GRID_VALUES_LOWER_BOUND = 10;
    private static final int GRID_VALUES_UPPER_BOUND = 100;  // exclusive
    static final int GRID_NUMBER_OF_VALUES = 36;
    static final int GRID_TARGET_NUMBER = 57;

    /**
     * Restrict from being constructed outside of class
     */
    private RandomGridGenerator() {
    }


    public int getGridHeight() {
        return GRID_HEIGHT;
    }

    public int getGridWidth() {
        return GRID_WIDTH;
    }

    public VisualSearchTaskGrid getNextGrid() {
        int[][] grid = new int[GRID_HEIGHT][GRID_WIDTH];

        // Get the indices where we'll populate numbers
        List<Integer> randomNumericalIndices = getUniqueRandomInts(GRID_NUMBER_OF_VALUES, 1, GRID_HEIGHT * GRID_WIDTH, new HashSet<Integer>());

        // Get the values we'll populate at these incidies
        boolean shouldIncludeTargetNumberInGrid = shouldIncludeTargetNumberInGrid();
        List<Integer> randomValues = getRandomValues(shouldIncludeTargetNumberInGrid);

        // Just verify a few important things
        Assert.that(randomNumericalIndices.size() == GRID_NUMBER_OF_VALUES);
        Assert.that(randomNumericalIndices.size() == randomValues.size());
        if (shouldIncludeTargetNumberInGrid) {
            Assert.that(Collections.frequency(randomValues, GRID_TARGET_NUMBER) == 1);
        }

        // Populate the grid
        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {
                int numericalIndex = (GRID_WIDTH * i) + (j + 1);

                int indexOfNumericalIndexInRandomIndices = randomNumericalIndices.indexOf(numericalIndex);
                if (indexOfNumericalIndexInRandomIndices == -1) {
                    continue;
                }

                // Fetch the corresponding random value
                int randomValue = randomValues.get(indexOfNumericalIndexInRandomIndices);

                // Set the value in the grid
                grid[i][j] = randomValue;
            }
        }

        if (shouldIncludeTargetNumberInGrid) {
            return new VisualSearchTaskGrid(grid, GRID_NUMBER_OF_VALUES, RandomGridGenerator.GRID_TARGET_NUMBER);
        } else {
            return new VisualSearchTaskGrid(grid, GRID_NUMBER_OF_VALUES);
        }
    }

    /**
     * Gets the random values for populating the grid depending on whether the target number should
     * be included in the List
     * @return The List of random values
     */
    private List<Integer> getRandomValues(boolean shouldIncludeTargetNumberInGrid) {
        // Get the values we'll populate at these incidies
        Set<Integer> excludedValues = new HashSet<Integer>() {{ add(GRID_TARGET_NUMBER); }};

        List<Integer> randomValues;
        if (shouldIncludeTargetNumberInGrid) {
            // Get random values with one less element than required
            randomValues = getUniqueRandomInts(GRID_NUMBER_OF_VALUES - 1, GRID_VALUES_LOWER_BOUND, GRID_VALUES_UPPER_BOUND, excludedValues);

            // Insert the target number at a random location
            int randomIndex = getNextRandom(0, randomValues.size());
            randomValues.add(randomIndex, GRID_TARGET_NUMBER);
        } else {
            // Get all the random values needed
            randomValues = getUniqueRandomInts(GRID_NUMBER_OF_VALUES, GRID_VALUES_LOWER_BOUND, GRID_VALUES_UPPER_BOUND, excludedValues);
        }

        Assert.that(randomValues.size() == GRID_NUMBER_OF_VALUES);

        return randomValues;
    }

    private boolean shouldIncludeTargetNumberInGrid() {
        return sRandom.nextBoolean();
    }


    @NonNull
    private static List<Integer> getUniqueRandomInts(int count, int lowerBound, int upperBound /* exclusive */, Set<Integer> excludingValues) {
        if (upperBound - lowerBound < count) {
            throw new IllegalArgumentException();
        }

        // Get unique sRandom ints
        Set<Integer> selectedNumbers = new HashSet<>();
        while (selectedNumbers.size() < count) {
            int randomValue = getNextRandom(lowerBound, upperBound);
            // Don't include this number if it is excluded
            if (excludingValues.contains(randomValue)) {
                continue;
            }
            selectedNumbers.add(randomValue);
        }

        return new ArrayList<>(selectedNumbers);
    }

    private static int getNextRandom(int lowerBound, int upperBound) {
        return lowerBound + sRandom.nextInt(upperBound - lowerBound);
    }

    private static void verify(boolean condition) {
        if (!condition) {
            throw new RuntimeException("Condition failed to be satisfied.");
        }
    }
}
