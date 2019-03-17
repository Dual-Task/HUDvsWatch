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
    private static final String TAG = "|ClientBluetooth|";

    private BluetoothEventsListener.CONNECTION_STATE state;

    private BluetoothAdapter btAdapter;
    private BluetoothDevice mDevice;
    private BluetoothSocket btSocket;

    private String btAddress;
    private UUID rfCommServiceRecordUUID;

    private ConnectThread connectThread;
    private BluetoothCommunicationThread commThread;

    private BluetoothEventsListener mBluetoothEventsListener;


    public BluetoothClient(UUID rfCommServiceRecordUUID, BluetoothEventsListener bluetoothEventsListener) {
        this.rfCommServiceRecordUUID = rfCommServiceRecordUUID;
        this.mBluetoothEventsListener = bluetoothEventsListener;
        Log.i(TAG, "Creating new ClientBluetooth instance.");
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        state = BluetoothEventsListener.CONNECTION_STATE.DISCONNECTED;
    }


    //LIFECYCLE
    public void connectToServer(String serverMacAddress) {
        btAddress = serverMacAddress;
        mDevice = getBluetoothDevice();
        Log.d(TAG, "Attempting to connectToServer via bluetooth.");
        if (mDevice == null) {
            Log.e(TAG, "Failed to find bluetooth device.");
            return;
        }
        if (connectThread != null) {
            Log.e(TAG, "A connectToServer thread was already running. Cancelling...");
            connectThread.cancel();
            Log.d(TAG, "Canceled.");
        }
        connectThread = new ConnectThread();
        connectThread.start();
    }

    public void disconnect() {
        Log.d(TAG, "Disconnecting threads.");
        if (connectThread != null) {
            Log.d(TAG, "Cancelling the connectToServer thread...");
            connectThread.cancel();
            Log.d(TAG, "Canceled.");
            connectThread = null;
        }
        if (commThread != null) {
            Log.d(TAG, "Cancelling the communication thread...");
            commThread.cancel();
            Log.d(TAG, "Canceled.");
            commThread = null;
        }
    }

    public boolean isConnected() {
        return state == BluetoothEventsListener.CONNECTION_STATE.CONNECTED;
    }
    //END OF LIFECYCLE


    //ACTIONS
    private void initiateCommunication(BluetoothSocket socket) {
        if (commThread != null) {
            Log.e(TAG, "A communication thread was already running. Cancelling...");
            commThread.cancel();
            Log.d(TAG, "Canceled.");
        }
        Log.d(TAG, "Initiating the Communication thread.");
        commThread = new BluetoothCommunicationThread(mBluetoothEventsListener, socket);
        commThread.start();
    }

    private BluetoothDevice getBluetoothDevice() {
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        Log.i(TAG, "Searching all " + pairedDevices.size() + " bluetooth devices.");
        for (BluetoothDevice device : pairedDevices) {
            if (device.getAddress().toUpperCase().equals(btAddress)) {
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
                btSocket = mDevice.createRfcommSocketToServiceRecord(rfCommServiceRecordUUID);
                Log.i(TAG, "Client Socket generated.");
            } catch (IOException e) {
                Log.e(TAG, "Failed to create the client socket.", e);
            }
        }

        public void run() {
            btAdapter.cancelDiscovery();

            try {
                Log.d(TAG, "Attempting to connectToServer with Server.");
                btSocket.connect();
                Log.d(TAG, "Connected. Initiating Communications");
                initiateCommunication(btSocket);
            } catch (Exception e) {
                Log.e(TAG, "Failed to connectToServer with Server.", e);
                cancel();
            }
        }

        // Closes the client socket and causes the thread to finish.
        void cancel() {
            Log.d(TAG, "Canceling connected thread.");
            state = BluetoothEventsListener.CONNECTION_STATE.DISCONNECTED;
            if (btSocket != null) {
                try {
                    btSocket.close();
                    btSocket = null;
                    Log.d(TAG, "Closed Client Socket successfully.");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to close Client Socket.", e);
                }
            }
        }
    }
}

