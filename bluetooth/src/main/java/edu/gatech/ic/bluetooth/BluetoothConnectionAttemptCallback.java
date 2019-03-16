package edu.gatech.ic.bluetooth;

/**
 * Created by p13i on 10/31/18.
 */

public interface BluetoothConnectionAttemptCallback {
    void connected();
    void disconnected();
}
