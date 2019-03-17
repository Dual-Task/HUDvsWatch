package edu.gatech.ic.hudvswatch.activities;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import edu.gatech.ic.bluetooth.BluetoothEventsListener;
import edu.gatech.ic.bluetooth.BluetoothServer;
import edu.gatech.ic.hudvswatch.R;
import edu.gatech.ic.hudvswatch.shared.Shared;

public class DeviceActivity extends AppCompatActivity {
    static final String TAG = DeviceActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        // Populate paired devices
        LinearLayout deviceListLinearLayout = findViewById(R.id.devices_list);
        for (BluetoothDevice device : BluetoothServer.getPairedDevices()) {
            deviceListLinearLayout.addView(getDeviceLayoutForDevice(device));
        }
    }

    /**
     * An absurdly-compact method that will create a new layout for each connected Bluetooth device
     * @param device
     * @return
     */
    @NonNull
    private RelativeLayout getDeviceLayoutForDevice(final BluetoothDevice device) {

        return new RelativeLayout(this) {{

            // No background
            setBackgroundColor(Color.TRANSPARENT);

            // Appropriate margins and padding
            setPadding(5, 5, 5, 5);
            setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) {{
                setMargins(0, 0, 0, 5);
            }});

            // Create a bold device name
            final AppCompatTextView deviceTextView = new AppCompatTextView(DeviceActivity.this) {{
                setId(View.generateViewId());
                setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                setText(device.getName());
                setTypeface(getTypeface(), Typeface.BOLD);
            }};

            final AppCompatTextView deviceStatusView = new AppCompatTextView(DeviceActivity.this) {{
                setId(View.generateViewId());
                setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) {{
                    addRule(RelativeLayout.RIGHT_OF, deviceTextView.getId());
                    setMargins(10, 0, 0, 0);
                }});
                setText("Disconnected");
                setTypeface(getTypeface(), Typeface.ITALIC);
            }};

            final AppCompatTextView deviceAddressView = new AppCompatTextView(DeviceActivity.this) {{
                setId(View.generateViewId());
                setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) {{
                   addRule(RelativeLayout.BELOW, deviceStatusView.getId());
                }});
                setText(device.getAddress());
                setTypeface(Typeface.MONOSPACE);
            }};

            final AppCompatButton connectButton = new AppCompatButton(DeviceActivity.this) {{
                setId(View.generateViewId());
                setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) {{
                    addRule(RelativeLayout.ALIGN_PARENT_END);
                }});
                setGravity(Gravity.CENTER_VERTICAL);
                setText("Connect");
            }};

            connectButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    connectButton.setText("Connecting...");
                    connectToDevice(device, new BluetoothEventsListener() {
                        @Override
                        public void onConnected() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    deviceStatusView.setText("Connected");
                                    connectButton.setText("Disconnect");
                                }
                            });
                        }

                        @Override
                        public void onDisconnected() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    deviceStatusView.setText("Disconnected");
                                    connectButton.setText("Connect");
                                }
                            });
                        }

                        @Override
                        public void onReceive(byte[] bytes) {

                        }
                    });
                }
            });

            addView(deviceTextView);
            addView(deviceStatusView);
            addView(deviceAddressView);
            addView(connectButton);
        }};
    }

    private void connectToDevice(BluetoothDevice device, BluetoothEventsListener bluetoothEventsListener) {
        BluetoothServer bluetoothServer = new BluetoothServer(bluetoothEventsListener);
        bluetoothServer.setAddress(device.getAddress(), Shared.SESSION_UUID);
        bluetoothServer.listen();
        Log.d(TAG, "Connected to " + device.getName());
    }

}
