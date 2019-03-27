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
    private static final String TAG = BluetoothServer.class.getName();

    private BluetoothEventsListener mBluetoothEventsListener;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mBluetoothSocket;
    private BluetoothServerSocket mBluetoothServerSocket;

    private String mRfCommServiceRecordName;
    private UUID mRfCommServiceRecordUUID;

    private ListenThread mListenThread;
    private BluetoothCommunicationThread mCommunicationThread;


    public BluetoothServer(String rfCommServiceRecordName, UUID rfCommServiceRecordUUID) {
        mRfCommServiceRecordName = rfCommServiceRecordName;
        mRfCommServiceRecordUUID = rfCommServiceRecordUUID;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void setBluetoothEventsListener(BluetoothEventsListener bluetoothEventsListener) {
        mBluetoothEventsListener = bluetoothEventsListener;
    }

    public void listen() {
        if (mListenThread != null) {
            Log.e(TAG, "A listen thread was already running. Cancelling...");
            mListenThread.cancel();
            Log.d(TAG, "Canceled.");
        }
        mListenThread = new ListenThread();
        mListenThread.start();
    }

    public void disconnect() {
        Log.d(TAG, "Disconnecting threads.");
        if (mListenThread != null) {
            Log.d(TAG, "Cancelling the listen thread...");
            mListenThread.cancel();
            Log.d(TAG, "Canceled.");
            mListenThread = null;
        }
        if (mCommunicationThread != null) {
            Log.d(TAG, "Cancelling the communication thread...");
            mCommunicationThread.cancel();
            Log.d(TAG, "Canceled.");
            mCommunicationThread = null;
        }
    }

    private void startCommunicationThread() {
        if (mCommunicationThread != null) {
            Log.e(TAG, "A communication thread was already running. Cancelling...");
            mCommunicationThread.cancel();
            Log.d(TAG, "Canceled.");
        }
        Log.d(TAG, "Initiating the Communication thread.");
        mCommunicationThread = new BluetoothCommunicationThread(mBluetoothSocket);
        mCommunicationThread.setBluetoothEventsListener(mBluetoothEventsListener);
        mCommunicationThread.start();
    }

    public BluetoothCommunicationThread getCommunicationThread() {
        return mCommunicationThread;
    }

    private class ListenThread extends Thread {
        private boolean mIsRunning;

        public ListenThread() {
            try {
                mBluetoothServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(mRfCommServiceRecordName, mRfCommServiceRecordUUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket failed.");
                cancel();
            }
        }

        public void run() {
            mIsRunning = true;

            while (mIsRunning) {
                try {
                    Log.d(TAG, "Listening for devices..");
                    mBluetoothSocket = mBluetoothServerSocket.accept(30000);
                } catch (Exception e) {
                    Log.e(TAG, "Timed-out (30000).");
                }

                if (mBluetoothSocket != null) {
                    Log.d(TAG, "Initiating Communications with accepted client.");
                    startCommunicationThread();
                    mIsRunning = false;
                }
            }
            Log.d(TAG, "Listen thread exits the loop.");
            cancelServerSocket();
        }

        void cancel() {
            Log.d(TAG, "Canceling listening thread.");
            mIsRunning = false;
            cancelServerSocket();
            if (mBluetoothSocket != null) {
                try {
                    mBluetoothSocket.close();
                    mBluetoothSocket = null;
                    Log.d(TAG, "Closed BT Socket successfully.");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to close BT Socket.", e);
                }
            }
        }

        void cancelServerSocket() {
            if (mBluetoothServerSocket != null) {
                try {
                    mBluetoothServerSocket.close();
                    mBluetoothServerSocket = null;
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
