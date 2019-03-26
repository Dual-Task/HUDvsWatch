package edu.gatech.ic.hudvswatch;

import android.support.wearable.activity.WearableActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
        // Keep the screen on: https://developer.android.com/training/scheduling/wakelock.html#screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
                connectButton.setEnabled(false);

                BluetoothClient bluetoothClient = new BluetoothClient(Shared.BLUETOOTH.RF_COMM_SERVICE_RECORD.UUID, new BluetoothEventsListener() {
                    @Override
                    public void onConnected() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusTextView.setText("Connected.");

                                connectButton.setText("Disconnect");
                                connectButton.setEnabled(true);
                            }
                        });
                    }

                    @Override
                    public void onDisconnected() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusTextView.setText("Disconnected.");

                                connectButton.setText("Connect");
                                connectButton.setEnabled(true);
                            }
                        });
                    }

                    @Override
                    public void onReceive(final byte[] bytes) {

                        String message = new String(bytes);
                        if (message.equals("Countdown started")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    statusTextView.setText("Countdown started");
                                }
                            });
                        }
                        if (message.equals("Study started")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    statusTextView.setText("Study started");
                                }
                            });
                        } else if (message.startsWith("Notification: ")) {
                            final int number = Integer.parseInt(message.substring(14, 15));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    statusTextView.setText(String.format("Received notification: %d", number));
                                }
                            });
                        } else if (message.equals("Completed")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    statusTextView.setText("Completed");
                                }
                            });
                        }

                    }
                });
                bluetoothClient.connectToServer(Shared.BLUETOOTH.MAC_ADDRESSES.MOBILE);
            }
        });
    }
}
