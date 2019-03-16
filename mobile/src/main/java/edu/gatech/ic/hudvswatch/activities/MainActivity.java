package edu.gatech.ic.hudvswatch.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.util.List;

import edu.gatech.ic.hudvswatch.R;
import edu.gatech.ic.hudvswatch.utils.StudyRunInformation;

public class MainActivity extends AppCompatActivity {

    // Used for logging
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add all the available study conditions to the drop-down selector
        addConditionsToSpinner(StudyRunInformation.AVAILABLE_CONDITIONS);
    }

    /**
     * When the "START" button is clicked, get the study run information, and move to the StudyActivity
     * @param v
     */
    public void onStartButtonClick(View v) {
        final StudyRunInformation studyRunInformation = getStudyRunInformationFromActivity();

        Log.d(TAG, studyRunInformation.toString());

        startActivity(new Intent(this, StudyActivity.class) {{
            putExtra("studyRunInformation", studyRunInformation);
        }});
    }

    public void onDevicesButtonClick(View v) {
        startActivity(new Intent(this, DeviceActivity.class));
    }

    public StudyRunInformation getStudyRunInformationFromActivity() {
        String subjectId = ((EditText) findViewById(R.id.subject_id_text)).getText().toString();
        String condition = ((Spinner) findViewById(R.id.conditions_spinner)).getSelectedItem().toString();
        boolean isTraining = !((ToggleButton) findViewById(R.id.study_mode_toggle_button)).isChecked();
        return new StudyRunInformation(subjectId, condition, isTraining);
    }

    public void addConditionsToSpinner(List<String> studyConditions) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                studyConditions) {{
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }};

        Spinner conditionsSpinner = findViewById(R.id.conditions_spinner);
        conditionsSpinner.setAdapter(dataAdapter);
    }
}
