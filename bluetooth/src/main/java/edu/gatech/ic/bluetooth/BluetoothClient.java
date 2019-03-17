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
    private UUID deviceUUID;

    private ConnectThread connectThread;
    private BluetoothCommunicationThread commThread;

    private BluetoothEventsListener mBluetoothEventsListener;


    public BluetoothClient(BluetoothEventsListener bluetoothEventsListener) {
        this.mBluetoothEventsListener = bluetoothEventsListener;
        Log.i(TAG, "Creating new ClientBluetooth instance.");
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        state = BluetoothEventsListener.CONNECTION_STATE.DISCONNECTED;
    }

    public void setAddress(String addrs, UUID uuid) {
        btAddress = addrs;
        deviceUUID = uuid;
    }


    //LIFECYCLE
    public void connect() {
        mDevice = getBluetoothDevice();
        Log.d(TAG, "Attempting to connect via bluetooth.");
        if (mDevice == null) {
            Log.e(TAG, "Failed to find bluetooth device.");
            return;
        }
        if (connectThread != null) {
            Log.e(TAG, "A connect thread was already running. Cancelling...");
            connectThread.cancel();
            Log.d(TAG, "Canceled.");
        }
        connectThread = new ConnectThread();
        connectThread.start();
    }

    public void disconnect() {
        Log.d(TAG, "Disconnecting threads.");
        if (connectThread != null) {
            Log.d(TAG, "Cancelling the connect thread...");
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
                btSocket = mDevice.createRfcommSocketToServiceRecord((deviceUUID));
                Log.i(TAG, "Client Socket generated.");
            } catch (IOException e) {
                Log.e(TAG, "Failed to create the client socket.", e);
            }
        }

        public void run() {
            btAdapter.cancelDiscovery();

            try {
                Log.d(TAG, "Attempting to connect with Server.");
                btSocket.connect();
                Log.d(TAG, "Connected. Initiating Communications");
                initiateCommunication(btSocket);
            } catch (Exception e) {
                Log.e(TAG, "Failed to connect with Server.", e);
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

