package edu.gatech.ic.hudvswatch.utils;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by p13i on 10/29/18.
 */

public class RandomGridGenerator {
    private static final RandomGridGenerator ourInstance = new RandomGridGenerator();

    public static RandomGridGenerator getInstance() {
        return ourInstance;
    }


    private static final int GRID_HEIGHT = 10;
    private static final int GRID_WIDTH = 10;
    private static final int GRID_VALUES_LOWER_BOUND = 10;
    private static final int GRID_VALUES_UPPER_BOUND = 100;  // exclusive
    private static final int GRID_COUNT = 5;

    private static Random random = new Random(42);

    private RandomGridGenerator() {
    }


    public int getGridHeight() {
        return GRID_HEIGHT;
    }

    public int getGridWidth() {
        return GRID_WIDTH;
    }

    public int[][] getNextGrid() {
        int[][] grid = new int[GRID_HEIGHT][GRID_WIDTH];

        // Get the indices where we'll populate numbers
        List<Integer> randomNumericalIndices = getUniqueRandomInts(GRID_COUNT, 1, GRID_HEIGHT * GRID_WIDTH);

        // Get the values we'll populate at these incidies
        List<Integer> randomValues = getUniqueRandomInts(GRID_COUNT, GRID_VALUES_LOWER_BOUND, GRID_VALUES_UPPER_BOUND);

        // Populate the grid
        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {
                int numericalIndex = (GRID_WIDTH * i) + (j + 1);
//                Log.d("tag", String.format("%d, %d = %d", i, j, numericalIndex));

                int indexOfNumericalIndexInRandomIndices = randomNumericalIndices.indexOf(numericalIndex);
                if (indexOfNumericalIndexInRandomIndices == -1) {
                    continue;
                }

                // Fetch the corresponding random value
                int randomValue = randomValues.get(indexOfNumericalIndexInRandomIndices);

                // Set the value in the grid
                grid[i][j] = randomValue;
//                Log.d("tag", String.format("%d, %d = %d - written", i, j, randomValue));
            }
        }
        return grid;
    }


    @NonNull
    private List<Integer> getUniqueRandomInts(int count, int lowerBound, int upperBound /* exclusive */) {
        if (upperBound - lowerBound < count) {
            throw new IllegalArgumentException();
        }

        // Get unique random ints
        Set<Integer> selectedNumbers = new HashSet<>();
        while (selectedNumbers.size() < count) {
            int randomValue = getNextRandom(lowerBound, upperBound);
            selectedNumbers.add(randomValue);
        }

        return new ArrayList<>(selectedNumbers);
    }

    private int getNextRandom(int lowerBound, int upperBound) {
        return lowerBound + random.nextInt(upperBound - lowerBound);
    }
}
