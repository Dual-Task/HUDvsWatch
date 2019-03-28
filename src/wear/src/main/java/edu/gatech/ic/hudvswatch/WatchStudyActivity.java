package edu.gatech.ic.hudvswatch;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.WindowManager;
import android.widget.TextView;

import edu.gatech.ic.bluetooth.BluetoothEventsListener;
import edu.gatech.ic.hudvswatch.utils.SharedBluetoothDeviceManager;

public class WatchStudyActivity extends WearableActivity implements BluetoothEventsListener {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_study);

        mTextView = (TextView) findViewById(R.id.watch_study_activity_text_view);

        // Enables Always-on
        setAmbientEnabled();

        // Keep the screen on: https://developer.android.com/training/scheduling/wakelock.html#screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mTextView.setText("Connected.");

        SharedBluetoothDeviceManager.getInstance()
                .getSharedBluetoothClient()
                .getCommunicationThread()
                .setBluetoothEventsListener(this);
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onReceive(byte[] bytes) {

        String message = new String(bytes);
        if (message.equals("Countdown started")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextView.setTextSize(16f);
                    mTextView.setText("Countdown started");
                }
            });
        } else if (message.equals("Study started")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextView.setTextSize(16f);
                    mTextView.setText("Study started");
                }
            });
        } else if (message.startsWith("Notification: ")) {
            final int number = Integer.parseInt(message.substring(14, 15));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextView.setTextSize(64f);
                    mTextView.setText(Integer.toString(number));
                }
            });
        } else if (message.equals("Completed")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextView.setTextSize(16f);
                    mTextView.setText("Completed");
                }
            });
        }

    }
}
