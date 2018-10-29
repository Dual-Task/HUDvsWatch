package edu.gatech.ic.hudvswatch;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    List<String> studyConditions = new ArrayList<String>() {{
        add("");
        add("Visual Search");
        add("HUD");
        add("Watch");
        add("Visual Search + HUD");
        add("Visual Search + Watch");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        addConditionsToSpinner();
    }

    public void onStartButtonClick(View v) {
        final String subjectId = ((EditText) findViewById(R.id.subject_id_text)).getText().toString();
        final String condition = ((Spinner) findViewById(R.id.conditions_spinner)).getSelectedItem().toString();
        final Boolean isTraining = !((ToggleButton) findViewById(R.id.study_mode_toggle_button)).isChecked();

        Log.d(TAG, String.format("Subject ID = %s, Condition = %s, Is Training = %s", subjectId, condition, isTraining.toString()));

        Intent intent = new Intent(this, StudyActivity.class) {{
            putExtra("subjectId", subjectId);
            putExtra("condition", condition);
            putExtra("isTraining", isTraining);
        }};
        startActivity(intent);
    }

    public void addConditionsToSpinner() {
        Spinner conditionsSpinner = findViewById(R.id.conditions_spinner);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                studyConditions);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionsSpinner.setAdapter(dataAdapter);
    }
}
