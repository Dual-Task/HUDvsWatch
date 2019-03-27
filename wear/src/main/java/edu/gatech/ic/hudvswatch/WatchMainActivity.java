package edu.gatech.ic.hudvswatch;

import android.content.Intent;
import android.support.wearable.activity.WearableActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import edu.gatech.ic.bluetooth.BluetoothClient;
import edu.gatech.ic.bluetooth.BluetoothEventsListener;
import edu.gatech.ic.hudvswatch.shared.Shared;
import edu.gatech.ic.hudvswatch.utils.SharedBluetoothDeviceManager;

public class WatchMainActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_main);

        setupViews();

        // Enables Always-on
        setAmbientEnabled();

        // Keep the screen on: https://developer.android.com/training/scheduling/wakelock.html#screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    void setupViews() {
        final TextView statusTextView = findViewById(R.id.status_text_view);

        final Button connectButton = findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusTextView.setText("Connecting...");
                connectButton.setEnabled(false);

                BluetoothClient bluetoothClient = new BluetoothClient(Shared.BLUETOOTH.RF_COMM_SERVICE_RECORD.UUID);
                bluetoothClient.setBluetoothEventsListener(new BluetoothEventsListener() {
                    @Override
                    public void onConnected() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(WatchMainActivity.this, WatchStudyActivity.class));
                            }
                        });
                    }

                    @Override
                    public void onDisconnected() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusTextView.setText("Disconnected...");
                            }
                        });
                    }

                    @Override
                    public void onReceive(final byte[] bytes) {

                    }
                });
                bluetoothClient.connectToServer(Shared.BLUETOOTH.MAC_ADDRESSES.MOBILE);

                SharedBluetoothDeviceManager.getInstance().setSharedBluetoothClient(bluetoothClient);
            }
        });
    }
}
