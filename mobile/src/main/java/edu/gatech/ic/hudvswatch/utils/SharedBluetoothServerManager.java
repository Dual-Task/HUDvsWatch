package edu.gatech.ic.hudvswatch.utils;

import edu.gatech.ic.bluetooth.BluetoothServer;

public class SharedBluetoothServerManager {
    private static final SharedBluetoothServerManager ourInstance = new SharedBluetoothServerManager();

    public static SharedBluetoothServerManager getInstance() {
        return ourInstance;
    }

    private String mDeviceName;
    private BluetoothServer mBluetoothServer;

    private SharedBluetoothServerManager() {
    }

    public void setBluetoothServer(BluetoothServer bluetoothServer) {
        mBluetoothServer = bluetoothServer;
    }

    public BluetoothServer getBluetoothServer() {
        return mBluetoothServer;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public void setDeviceName(String deviceName) {
        this.mDeviceName = deviceName;
    }
}
