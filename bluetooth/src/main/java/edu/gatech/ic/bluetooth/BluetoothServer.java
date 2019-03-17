package edu.gatech.ic.bluetooth;

/**
 * Created by p13i on 10/31/18.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;


/**
 * This Class handles the bluetooth connection with the mobile app.
 * Once it receives a byte stream, it sends it to Communication Handler
 */

//TO DO
//1. setDeviceToListen(); Handle failed BT search.


public class BluetoothServer {
    private static final String TAG = "|ServerBluetooth|";

    private BluetoothEventsListener.CONNECTION_STATE state;

    private BluetoothEventsListener mBluetoothEventsListener;

    private BluetoothAdapter btAdapter;
    private BluetoothDevice mDevice;
    private BluetoothSocket btSocket;
    private BluetoothServerSocket btServerSocket;

    private String rfCommServiceRecordName;
    private UUID rfCommServiceRecordUUID;

    private ListenThread listenThread;
    private BluetoothCommunicationThread commThread;


    public BluetoothServer(String rfCommServiceRecordName, UUID rfCommServiceRecordUUID, BluetoothEventsListener bluetoothEventsListener) {
        this.rfCommServiceRecordName = rfCommServiceRecordName;
        this.rfCommServiceRecordUUID = rfCommServiceRecordUUID;
        mBluetoothEventsListener = bluetoothEventsListener;

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        state = BluetoothEventsListener.CONNECTION_STATE.DISCONNECTED;
    }


    public void listen() {
        if (listenThread != null) {
            Log.e(TAG, "A listen thread was already running. Cancelling...");
            listenThread.cancel();
            Log.d(TAG, "Canceled.");
        }
        listenThread = new ListenThread();
        listenThread.start();
    }

    public void disconnect() {
        Log.d(TAG, "Disconnecting threads.");
        if (listenThread != null) {
            Log.d(TAG, "Cancelling the listen thread...");
            listenThread.cancel();
            Log.d(TAG, "Canceled.");
            listenThread = null;
        }
        if (commThread != null) {
            Log.d(TAG, "Cancelling the communication thread...");
            commThread.cancel();
            Log.d(TAG, "Canceled.");
            commThread = null;
        }
    }

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

    public BluetoothCommunicationThread getCommThread() {
        return commThread;
    }

    private class ListenThread extends Thread {
        private boolean isRunning;

        public ListenThread() {
            try {
                btServerSocket = btAdapter.listenUsingRfcommWithServiceRecord(rfCommServiceRecordName, rfCommServiceRecordUUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket failed.");
                cancel();
            }
        }

        public void run() {
            isRunning = true;

            while (isRunning) {
                try {
                    Log.d(TAG, "Listening for devices..");
                    btSocket = btServerSocket.accept(30000);
                } catch (Exception e) {
                    Log.e(TAG, "Timed-out (30000).");
                }

                if (btSocket != null) {
                    Log.d(TAG, "Initiating Communications with accepted client.");
                    initiateCommunication(btSocket);
                    isRunning = false;
                }
            }
            Log.d(TAG, "Listen thread exits the loop.");
            cancelServerSocket();
        }

        void cancel() {
            Log.d(TAG, "Canceling listening thread.");
            isRunning = false;
            state = BluetoothEventsListener.CONNECTION_STATE.DISCONNECTED;
            cancelServerSocket();
            if (btSocket != null) {
                try {
                    btSocket.close();
                    btSocket = null;
                    Log.d(TAG, "Closed BT Socket successfully.");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to close BT Socket.", e);
                }
            }
        }

        void cancelServerSocket() {
            if (btServerSocket != null) {
                try {
                    btServerSocket.close();
                    btServerSocket = null;
                    Log.d(TAG, "Closed Server Socket successfully.");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to close Server Socket.", e);
                }
            }
        }
    }

    public static Set<BluetoothDevice> getPairedDevices() {
        return BluetoothAdapter.getDefaultAdapter().getBondedDevices();
    }
}