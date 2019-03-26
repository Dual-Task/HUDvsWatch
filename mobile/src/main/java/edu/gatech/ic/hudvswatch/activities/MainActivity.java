package edu.gatech.ic.hudvswatch.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
import edu.gatech.ic.hudvswatch.models.StudyRunInformation;
import edu.gatech.ic.hudvswatch.utils.SharedBluetoothServerManager;

public class MainActivity extends AppCompatActivity {

    // Used for logging
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add all the available study conditions to the drop-down selector
        addConditionsToSpinner(StudyRunInformation.getAvailableConditionsAsStrings());
    }

    /**
     * When the "START" button is clicked, get the study run information, and move to the StudyActivity
     * @param v
     */
    public void onStartButtonClick(View v) {
        final StudyRunInformation studyRunInformation = getStudyRunInformationFromActivity();

        Log.d(TAG, studyRunInformation.toString());

        boolean canStartStudyAcviity = true;

        // Check if the required devices are connected before continuing
        if (studyRunInformation.doesConditionInvolveBluetoothDevice()) {
            if (!SharedBluetoothServerManager.getInstance().isDeviceConnected()) {
                // No device is connected but one is needed
                canStartStudyAcviity = false;
                new AlertDialog.Builder(this)
                        .setTitle("No device connected")
                        .setMessage(String.format("A Bluetooth device is required for this %s condition, but none is connected", studyRunInformation.getCondition()))
                        .setPositiveButton("Devices...", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(MainActivity.this, DeviceActivity.class));
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();
            } else if (!studyRunInformation.getRequiredDeviceName().equals(SharedBluetoothServerManager.getInstance().getDeviceName())) {
                // The right device is not connected
                canStartStudyAcviity = false;
                new AlertDialog.Builder(this)
                        .setTitle("Wrong device connected")
                        .setMessage(String.format("%s is required for this %s condition, but %s is connected", studyRunInformation.getRequiredDeviceName(), studyRunInformation.getCondition(), SharedBluetoothServerManager.getInstance().getDeviceName()))
                        .setPositiveButton("Devices...", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(MainActivity.this, DeviceActivity.class));
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();
            }
        }

        if (canStartStudyAcviity) {
            startActivity(new Intent(this, StudyActivity.class) {{
                putExtra("studyRunInformation", studyRunInformation);
            }});
        }
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
