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

    public void setConnectedDevice(String deviceName, BluetoothServer bluetoothServer) {
        mDeviceName = deviceName;
        mBluetoothServer = bluetoothServer;
        mIsDeviceConnected = true;
    }

    public BluetoothServer getBluetoothServer() {
        return mBluetoothServer;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public void clearDevice() {
        mDeviceName = null;
        mBluetoothServer = null;
        mIsDeviceConnected = false;
    }

    public boolean isDeviceConnected() {
        return mIsDeviceConnected;
    }
}
