package edu.gatech.ic.hudvswatch.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.gatech.ic.hudvswatch.shared.Shared;
import edu.gatech.ic.hudvswatch.utils.Assert;

/**
 * Created by p13i on 10/29/18.
 */

public class StudyRunInformation implements Serializable {
    public static class AvailableConditions {
        public static final String VISUAL_SEARCH = "Visual Search";
        public static final String HUD = "HUD";
        public static final String WATCH = "Watch";
        public static final String VISUAL_SEARCH_AND_HUD = "Visual Search + HUD";
        public static final String VISUAL_SEARCH_AND_WATCH = "Visual Search + Watch";
    }

    private String mSubjectId;
    private String mCondition;
    private boolean mIsTraining;
    private int mNumberOfVisualSearchTasks;
    private int mVisualSearchTaskDuration;

    public StudyRunInformation(String subjectId, String condition, boolean isTraining) {
        mSubjectId = subjectId;
        mCondition = condition;
        mIsTraining = isTraining;
        mNumberOfVisualSearchTasks = isTraining ? 5 : 10;
        mVisualSearchTaskDuration = 5;
    }

    /**
     * Source: https://stackoverflow.com/a/13783744
     * @return
     */
    public static List<String> getAvailableConditionsAsStrings() {
        return new ArrayList<String>() {{
            add(AvailableConditions.VISUAL_SEARCH);
            add(AvailableConditions.HUD);
            add(AvailableConditions.WATCH);
            add(AvailableConditions.VISUAL_SEARCH_AND_HUD);
            add(AvailableConditions.VISUAL_SEARCH_AND_WATCH);
        }};
    }

    @Override
    public String toString() {
        return String.format(
                "Subject ID: %s, Condition: %s, Is Training: %s, Visual Search Tasks: %d, Visual Search Task Duration: %d",
                mSubjectId, mCondition, mIsTraining, mNumberOfVisualSearchTasks, mVisualSearchTaskDuration);
    }

    public String toMultilineString() {
        return toString().replace(',', '\n');
    }

    public String getSubjectId() {
        return mSubjectId;
    }

    public String getCondition() {
        return mCondition;
    }

    public boolean isTraining() {
        return mIsTraining;
    }

    public String isTrainingAsString() {
        return mIsTraining ? "TRAINING" : "TESTING";
    }

    public int getNumberOfVisualSearchTasks() {
        return mNumberOfVisualSearchTasks;
    }

    public boolean doesConditionInvolveBluetoothDevice() {
        return !mCondition.equals(AvailableConditions.VISUAL_SEARCH);
    }

    public String getRequiredDeviceName() {
        Assert.that(doesConditionInvolveBluetoothDevice());
        if (mCondition.equals(AvailableConditions.HUD) || mCondition.equals(AvailableConditions.VISUAL_SEARCH_AND_HUD)) {
            return Shared.BLUETOOTH.DEVICE_NAMES.HUD;
        } else if (mCondition.equals(AvailableConditions.WATCH) || mCondition.equals(AvailableConditions.VISUAL_SEARCH_AND_WATCH)) {
            return Shared.BLUETOOTH.DEVICE_NAMES.WATCH;
        }
        Assert.fail();
        return null;
    }
}
