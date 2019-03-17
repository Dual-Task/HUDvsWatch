package edu.gatech.ic.hudvswatch.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by p13i on 10/29/18.
 */

public class StudyRunInformation implements Serializable {
    private static final List<String> AVAILABLE_CONDITIONS = new ArrayList<String>() {{
        add("Visual Search");
        add("HUD");
        add("Watch");
        add("Visual Search + HUD");
        add("Visual Search + Watch");
    }};

    private String subjectId;
    private String condition;
    private boolean isTraining;

    public StudyRunInformation(String subjectId, String condition, boolean isTraining) {
        this.subjectId = subjectId;
        this.condition = condition;
        this.isTraining = isTraining;
    }

    public static List<String> getAvailableConditions() {
        return AVAILABLE_CONDITIONS;
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
}
