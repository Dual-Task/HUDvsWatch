package edu.gatech.ic.hudvswatch.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import edu.gatech.ic.hudvswatch.utils.CanvasView;
import edu.gatech.ic.hudvswatch.R;
import edu.gatech.ic.hudvswatch.utils.StudyRunInformation;

public class StudyActivity extends AppCompatActivity {

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
        StudyRunInformation studyRunInformation = ((StudyRunInformation) getIntent().getSerializableExtra("studyRunInformation"));
        CanvasView canvasView = findViewById(R.id.signature_canvas);
        canvasView.setStudyRunInformation(studyRunInformation);


    }

}
