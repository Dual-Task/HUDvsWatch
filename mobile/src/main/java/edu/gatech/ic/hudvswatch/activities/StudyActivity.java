package edu.gatech.ic.hudvswatch.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import edu.gatech.ic.hudvswatch.R;
import edu.gatech.ic.hudvswatch.models.StudyRunInformation;
import edu.gatech.ic.hudvswatch.ui.ConfirmButton;
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

    @Override
    public void onVisualSearchTaskFirstStart() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mYesButton.setEnabled(true);
                mNoButton.setEnabled(true);
            }
        });
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
    }
}
