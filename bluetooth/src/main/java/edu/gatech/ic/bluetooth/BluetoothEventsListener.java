package edu.gatech.ic.bluetooth;

/**
 * Created by p13i on 10/31/18.
 */

public interface BluetoothEventsListener {
    void onConnected();
    void onDisconnected();
    void onReceive(byte[] bytes);
}
