package edu.gatech.ic.hudvswatch.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

import edu.gatech.ic.bluetooth.BluetoothServer;
import edu.gatech.ic.hudvswatch.R;
import edu.gatech.ic.hudvswatch.models.StudyRunInformation;
import edu.gatech.ic.hudvswatch.shared.Shared;
import edu.gatech.ic.hudvswatch.ui.ConfirmButton;
import edu.gatech.ic.hudvswatch.utils.SharedBluetoothServerManager;
import edu.gatech.ic.hudvswatch.views.VisualSearchView;

public class StudyActivity extends AppCompatActivity implements VisualSearchView.VisualSearchViewEventsListener {

    private static final String TAG = StudyActivity.class.getName();

    // Private members
    StudyRunInformation mStudyRunInformation;

    // UI Components
    VisualSearchView mVisualSearchView;
    TextView mConditionTextView;
    TextView mSubjectIdTextView;
    TextView mIsTrainingTextView;
    TextView mConfirmTextView;
    ConfirmButton mYesButton;
    ConfirmButton mNoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the orientation to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Hide Status Bar and Action Bar (https://developer.android.com/training/system-ui/status#41)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // https://stackoverflow.com/a/2868052
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // https://www.javatpoint.com/android-hide-title-bar-example
        getSupportActionBar().hide();

        // Call at after requesting full screen according to https://stackoverflow.com/questions/4250149/requestfeature-must-be-called-before-adding-content
        setContentView(R.layout.activity_study);

        // Get the run information from the intent
        mStudyRunInformation = ((StudyRunInformation) getIntent().getSerializableExtra("studyRunInformation"));

        bindLayoutToActivity();
        bindButtonsToCallbacks();
        setLayoutValuesToStudyRunInformation();
        bindActivityToListeners();

        ensureRequiredDevicesAreConnected();
    }

    private void bindLayoutToActivity() {
        mVisualSearchView = findViewById(R.id.visual_search_task_view);
        mConditionTextView = findViewById(R.id.study_run_condition);
        mSubjectIdTextView = findViewById(R.id.study_run_subject_id);
        mIsTrainingTextView = findViewById(R.id.study_run_is_training);
        mConfirmTextView = findViewById(R.id.confirm_target_number_response);
        mYesButton = findViewById(R.id.confirm_target_number_YES);
        mNoButton = findViewById(R.id.confirm_target_number_NO);
    }

    private void bindButtonsToCallbacks() {
        mYesButton.setDefaultText("YES");
        mYesButton.setOnConfirmedListener(new ConfirmButton.ConfirmButtonListener() {
            @Override
            public void onFirstTap() {
                // Reset the other button
                mNoButton.resetButton();
            }

            @Override
            public void onConfirmed() {
                mYesButton.resetButton();
                Log.i(TAG, "Yes button confirmed.");
                mVisualSearchView.startNewVisualSearchTaskAfterYesOrNoPressed();
            }
        });
        mNoButton.setDefaultText("NO");
        mNoButton.setOnConfirmedListener(new ConfirmButton.ConfirmButtonListener() {
            @Override
            public void onFirstTap() {
                // Reset the other button
                mYesButton.resetButton();
            }

            @Override
            public void onConfirmed() {
                mYesButton.resetButton();
                Log.i(TAG, "No button confirmed.");
                mVisualSearchView.startNewVisualSearchTaskAfterYesOrNoPressed();
            }
        });
        mYesButton.setEnabled(false);
        mNoButton.setEnabled(false);
    }

    private void setLayoutValuesToStudyRunInformation() {
        mConditionTextView.setText(mStudyRunInformation.getCondition());
        mSubjectIdTextView.setText(mStudyRunInformation.getSubjectId());
        mIsTrainingTextView.setText(mStudyRunInformation.isTrainingAsString());
    }

    private void bindActivityToListeners() {
        mVisualSearchView.setVisualSearchTaskEventsListener(this);
    }

    private void ensureRequiredDevicesAreConnected() {
        if (mStudyRunInformation.doesConditionInvolveBluetoothDevice())
            if (!SharedBluetoothServerManager.getInstance().isDeviceConnected()) {
                new AlertDialog.Builder(this)
                        .setTitle("No device connected")
                        .setMessage(String.format("A Bluetooth device is required for this %s condition, but none is connected", mStudyRunInformation.getCondition()))
                        .setPositiveButton("Devices...", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(StudyActivity.this, DeviceActivity.class));
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();
            } else if (!mStudyRunInformation.getRequiredDeviceName().equals(SharedBluetoothServerManager.getInstance().getDeviceName())) {
                new AlertDialog.Builder(this)
                        .setTitle("Wrong device connected")
                        .setMessage(String.format("%s is required for this %s condition, but %s is connected", mStudyRunInformation.getRequiredDeviceName(), mStudyRunInformation.getCondition(), SharedBluetoothServerManager.getInstance().getDeviceName()))
                        .setPositiveButton("Devices...", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(StudyActivity.this, DeviceActivity.class));
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();
            }
    }

    @Override
    public void onCountdownStarted() {
        String message = "Countdown started";
        SharedBluetoothServerManager.getInstance()
                .getBluetoothServer()
                .getCommThread()
                .write(message.getBytes());
    }

    @Override
    public void onVisualSearchTaskFirstStart() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mYesButton.setEnabled(true);
                mNoButton.setEnabled(true);
            }
        });
        String message = "Study started";
        SharedBluetoothServerManager.getInstance()
                .getBluetoothServer()
                .getCommThread()
                .write(message.getBytes());
    }

    @Override
    public void onActivityShouldSendNotification(int number) {
        Log.i(TAG, String.format("Should send number: %d", number));
        if (mStudyRunInformation.doesConditionInvolveBluetoothDevice()) {
            String message = String.format("Notification: %d", number);
            Log.i(TAG, String.format("Sending message to %s: %s", SharedBluetoothServerManager.getInstance().getDeviceName(), message));
            SharedBluetoothServerManager.getInstance()
                    .getBluetoothServer()
                    .getCommThread()
                    .write(message.getBytes());
        }
    }

    @Override
    public void onVisualSearchTaskCompleted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mYesButton.setEnabled(false);
                mNoButton.setEnabled(false);
            }
        });

        String message = "Completed";
        SharedBluetoothServerManager.getInstance()
                .getBluetoothServer()
                .getCommThread()
                .write(message.getBytes());
    }
}
