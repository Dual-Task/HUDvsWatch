package edu.gatech.ic.hudvswatch.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.gatech.ic.hudvswatch.views.VisualSearchView;

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

    private String subjectId;
    private String condition;
    private boolean isTraining;

    public StudyRunInformation(String subjectId, String condition, boolean isTraining) {
        this.subjectId = subjectId;
        this.condition = condition;
        this.isTraining = isTraining;
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
        return String.format("Subject ID: %s, Condition: %s, Is Training: %s", getSubjectId(), getCondition(), isTraining());
    }

    public String getSubjectId() {
        return subjectId;
    }

    public String getCondition() {
        return condition;
    }

    public boolean isTraining() {
        return isTraining;
    }

    public String isTrainingAsString() {
        return isTraining ? "TRAINING" : "TESTING";
    }

    public boolean doesConditionInvolveBluetoothDevice() {
        return !condition.equals(AvailableConditions.VISUAL_SEARCH);
    }
}
