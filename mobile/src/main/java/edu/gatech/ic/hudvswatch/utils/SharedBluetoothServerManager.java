package edu.gatech.ic.hudvswatch.utils;

import edu.gatech.ic.bluetooth.BluetoothServer;

public class SharedBluetoothServerManager {
    private static final SharedBluetoothServerManager ourInstance = new SharedBluetoothServerManager();

    public static SharedBluetoothServerManager getInstance() {
        return ourInstance;
    }

    private String mDeviceName;
    private BluetoothServer mBluetoothServer;
    private boolean mIsDeviceConnected;

    private SharedBluetoothServerManager() {
    }

    public void setBluetoothServer(String deviceName, BluetoothServer bluetoothServer) {
        mBluetoothServer = bluetoothServer;
    }

    public BluetoothServer getBluetoothServer() {
        return mBluetoothServer;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public boolean isDeviceConnected() {
        return mIsDeviceConnected;
    }
}
