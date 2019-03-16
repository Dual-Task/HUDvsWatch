package edu.gatech.ic.hudvswatch.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by p13i on 10/29/18.
 */

public class StudyRunInformation implements Serializable {
    public static final List<String> AVAILABLE_CONDITIONS = new ArrayList<String>() {{
        add("Visual Search");
        add("HUD");
        add("Watch");
        add("Visual Search + HUD");
        add("Visual Search + Watch");
    }};

    String subjectId;
    String condition;
    boolean isTraining;

    public StudyRunInformation(String subjectId, String condition, boolean isTraining) {
        this.subjectId = subjectId;
        this.condition = condition;
        this.isTraining = isTraining;
    }

    @Override
    public String toString() {
        return String.format("Subject ID: %s, Condition: %s, Is Training: %s", subjectId, condition, isTraining);
    }
}
