package edu.gatech.ic.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by p13i on 10/31/18.
 */

public class BluetoothClient {
    private static final String TAG = BluetoothClient.class.getName();

    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;

    private UUID mRfCommServiceRecordUUID;

    private ConnectThread mConnectThread;
    private BluetoothCommunicationThread mBluetoothCommunicationThread;
    private BluetoothEventsListener mBluetoothEventsListener;


    public BluetoothClient(UUID rfCommServiceRecordUUID) {
        mRfCommServiceRecordUUID = rfCommServiceRecordUUID;
        Log.i(TAG, "Creating new ClientBluetooth instance.");
    }

    public void setBluetoothEventsListener(BluetoothEventsListener bluetoothEventsListener) {
        mBluetoothEventsListener = bluetoothEventsListener;
    }

    //LIFECYCLE
    public void connectToServer(String serverMacAddress) {
        mBluetoothDevice = getBluetoothServerDevice(serverMacAddress);
        Log.d(TAG, "Attempting to connectToServer via bluetooth.");
        if (mBluetoothDevice == null) {
            Log.e(TAG, "Failed to find bluetooth device.");
            return;
        }
        if (mConnectThread != null) {
            Log.e(TAG, "A connectToServer thread was already running. Cancelling...");
            mConnectThread.cancel();
            Log.d(TAG, "Canceled.");
        }
        mConnectThread = new ConnectThread();
        mConnectThread.start();
    }

    public void disconnect() {
        Log.d(TAG, "Disconnecting threads.");
        if (mConnectThread != null) {
            Log.d(TAG, "Cancelling the connectToServer thread...");
            mConnectThread.cancel();
            Log.d(TAG, "Canceled.");
            mConnectThread = null;
        }
        if (mBluetoothCommunicationThread != null) {
            Log.d(TAG, "Cancelling the communication thread...");
            mBluetoothCommunicationThread.cancel();
            Log.d(TAG, "Canceled.");
            mBluetoothCommunicationThread = null;
        }
    }

    //END OF LIFECYCLE


    //ACTIONS
    private void startCommunicationThread() {
        if (mBluetoothCommunicationThread != null) {
            Log.e(TAG, "A communication thread was already running. Cancelling...");
            mBluetoothCommunicationThread.cancel();
            Log.d(TAG, "Canceled.");
        }
        Log.d(TAG, "Initiating the Communication thread.");
        mBluetoothCommunicationThread = new BluetoothCommunicationThread(mBluetoothSocket);
        mBluetoothCommunicationThread.setBluetoothEventsListener(mBluetoothEventsListener);
        mBluetoothCommunicationThread.start();
    }

    private BluetoothDevice getBluetoothServerDevice(String serverMacAddress) {
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        Log.i(TAG, "Searching all " + pairedDevices.size() + " bluetooth devices.");
        for (BluetoothDevice device : pairedDevices) {
            if (device.getAddress().toUpperCase().equals(serverMacAddress)) {
                Log.i(TAG, "Device found.");
                return device;
            }
        }
        return null;
    }

    //END OF ACTIONS

    //THREADS

    private class ConnectThread extends Thread {

        public ConnectThread() {
            try {
                mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(mRfCommServiceRecordUUID);
                Log.i(TAG, "Client Socket generated.");
            } catch (IOException e) {
                Log.e(TAG, "Failed to create the client socket.", e);
            }
        }

        public void run() {
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

            try {
                Log.d(TAG, "Attempting to connectToServer with Server.");
                mBluetoothSocket.connect();
                Log.d(TAG, "Connected. Initiating Communications");
                startCommunicationThread();
            } catch (Exception e) {
                Log.e(TAG, "Failed to connectToServer with Server.", e);
                cancel();
            }
        }

        // Closes the client socket and causes the thread to finish.
        void cancel() {
            Log.d(TAG, "Canceling connected thread.");
            if (mBluetoothSocket != null) {
                try {
                    mBluetoothSocket.close();
                    mBluetoothSocket = null;
                    Log.d(TAG, "Closed Client Socket successfully.");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to close Client Socket.", e);
                }
            }
        }
    }

    public BluetoothCommunicationThread getCommunicationThread() {
        return mBluetoothCommunicationThread;
    }
}

