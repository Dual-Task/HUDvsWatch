package edu.gatech.ic.hudvswatch;

import android.support.wearable.activity.WearableActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.gatech.ic.bluetooth.BluetoothClient;
import edu.gatech.ic.bluetooth.BluetoothEventsListener;
import edu.gatech.ic.hudvswatch.shared.Shared;

public class WatchMainActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_main);

        setupViews();

        setAmbientEnabled();
    }

    void setupViews() {
        final TextView statusTextView = findViewById(R.id.status_text_view);

        final Button connectButton = findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusTextView.setText("Connecting...");
                BluetoothClient bluetoothClient = new BluetoothClient(Shared.BLUETOOTH.RF_COMM_SERVICE_RECORD.UUID, new BluetoothEventsListener() {
                    @Override
                    public void onConnected() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusTextView.setText("Connected.");
                            }
                        });
                    }

                    @Override
                    public void onDisconnected() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusTextView.setText("Disconnected.");
                            }
                        });
                    }

                    @Override
                    public void onReceive(final byte[] bytes) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusTextView.setText("Received: " + new String(bytes));
                            }
                        });
                    }
                });
                bluetoothClient.connectToServer(Shared.BLUETOOTH.MAC_ADDRESSES.MOBILE);
            }
        });
    }
}
