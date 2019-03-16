package edu.gatech.ic.hudvswatch;

import android.bluetooth.BluetoothDevice;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
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
import edu.gatech.ic.hudvswatch.shared.Shared;

public class DeviceActivity extends AppCompatActivity {
    static final String TAG = DeviceActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Populate paired devices
        LinearLayout deviceListLinearLayout = findViewById(R.id.devices_list);
        for (BluetoothDevice device : BluetoothServer.getPairedDevices()) {
            deviceListLinearLayout.addView(getDeviceLayout(device));
        }
    }

    @NonNull
    private RelativeLayout getDeviceLayout(final BluetoothDevice device) {

        return new RelativeLayout(this) {{

            setBackgroundColor(Color.TRANSPARENT);
            setPadding(5, 5, 5, 5);
            setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) {
                {
                    setMargins(0, 0, 0, 5);
                }
            });

            final AppCompatTextView deviceTextView = new AppCompatTextView(DeviceActivity.this) {{
                setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                setGravity(Gravity.CENTER_VERTICAL);
                setText(String.format("%s\nDisconnected\nMAC:%s", device.getName(), device.getAddress()));
            }};

            final AppCompatButton connectButton = new AppCompatButton(DeviceActivity.this) {{
                setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) {{
                    addRule(RelativeLayout.ALIGN_PARENT_END);
                }});
                setGravity(Gravity.CENTER_VERTICAL);
                setText("Connect");
            }};

            connectButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    connectToDevice(device, new BluetoothEventsListener() {
                        @Override
                        public void onConnected() {
                            deviceTextView.setText(String.format("%s\nConnected\nMAC:%s", device.getName(), device.getAddress()));
                            connectButton.setText("Disconnect");
                        }

                        @Override
                        public void onDisconnected() {
                            deviceTextView.setText(String.format("%s\nDisconnected\nMAC:%s", device.getName(), device.getAddress()));
                            connectButton.setText("Connect");
                        }

                        @Override
                        public void onReceive(byte[] bytes) {

                        }
                    });
                }
            });

            addView(deviceTextView);
            addView(connectButton);
        }};
    }

    private void connectToDevice(BluetoothDevice device, BluetoothEventsListener bluetoothEventsListener) {
        BluetoothServer bluetoothServer = new BluetoothServer(bluetoothEventsListener);
        bluetoothServer.setAddress(Shared.GLASS_BLUETOOTH_ADDRESS, Shared.GLASS_BLUETOOTH_ADDRESS);
        Log.d(TAG, "Connected to " + device.getName());
    }

}
