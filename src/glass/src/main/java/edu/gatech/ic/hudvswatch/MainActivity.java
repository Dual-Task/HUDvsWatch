package edu.gatech.ic.hudvswatch;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import edu.gatech.ic.bluetooth.BluetoothClient;
import edu.gatech.ic.bluetooth.BluetoothEventsListener;
import edu.gatech.ic.hudvswatch.shared.Shared;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        connectToMobile();
    }

    private void connectToMobile() {
        final TextView statusTextView = (TextView) findViewById(R.id.status);

        BluetoothClient bluetoothClient = new BluetoothClient(Shared.BLUETOOTH.RF_COMM_SERVICE_RECORD.UUID, new BluetoothEventsListener() {
            @Override
            public void onConnected() {
                statusTextView.setText("Connected to mobile.");
            }

            @Override
            public void onDisconnected() {
                statusTextView.setText("Disconnected from mobile.");
            }

            @Override
            public void onReceive(byte[] bytes) {
                statusTextView.setText("Received: " + new String(bytes));
            }
        });
        bluetoothClient.connectToServer(Shared.BLUETOOTH.MAC_ADDRESSES.MOBILE);
    }

}
