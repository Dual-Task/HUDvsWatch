package edu.gatech.ic.hudvswatch.utils;

import edu.gatech.ic.bluetooth.BluetoothClient;

public class SharedBluetoothDeviceManager {
    private static final SharedBluetoothDeviceManager ourInstance = new SharedBluetoothDeviceManager();

    public static SharedBluetoothDeviceManager getInstance() {
        return ourInstance;
    }

    private BluetoothClient mBluetoothClient;

    private SharedBluetoothDeviceManager() {
    }

    public void setSharedBluetoothClient(BluetoothClient bluetoothServer) {
        mBluetoothClient = bluetoothServer;
    }

    public BluetoothClient getSharedBluetoothClient() {
        return mBluetoothClient;
    }

    public void clearSharedBluetoothClient() {
        mBluetoothClient = null;
    }

    public boolean isSharedBluetoothClientConnected() {
        return mBluetoothClient != null;
    }
}
