package edu.gatech.ic.hudvswatch;

import java.io.Serializable;

/**
 * Created by p13i on 10/29/18.
 */

public class StudyRunInformation implements Serializable {
    public String subjectId;
    public String condition;
    public boolean isTraining;

    public StudyRunInformation(String subjectId, String condition, boolean isTraining) {
        this.subjectId = subjectId;
        this.condition = condition;
        this.isTraining = isTraining;
    }
}
