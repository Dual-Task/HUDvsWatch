package edu.gatech.ic.hudvswatch;

import android.support.wearable.activity.WearableActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WatchMainActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_main);

        setAmbientEnabled();
    }
}
