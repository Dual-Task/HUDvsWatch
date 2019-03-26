package edu.gatech.ic.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by p13i on 10/31/18.
 */

public class BluetoothCommunicationThread extends Thread {
    private static final String TAG = BluetoothCommunicationThread.class.getName();
    private BluetoothEventsListener mBluetoothListener;

    private BluetoothSocket mBluetoothSocket;
    private InputStream connectedInputStream;
    private OutputStream connectedOutputStream;

    private boolean isRunning;

    public BluetoothCommunicationThread(BluetoothEventsListener mBluetoothListener, BluetoothSocket mBluetoothSocket) {
        this.mBluetoothListener = mBluetoothListener;
        this.mBluetoothSocket = mBluetoothSocket;
        init();
    }

    public void init() {
        try {
            connectedInputStream = new DataInputStream(mBluetoothSocket.getInputStream());
            connectedOutputStream = new DataOutputStream(mBluetoothSocket.getOutputStream());
            Log.d(TAG, "I/O Streams created.");
        } catch (IOException e) {
            Log.e(TAG, "Failed to create the I/O streams.", e);
            cancel();
        }
    }

    public void run() {
        if (connectedInputStream == null)
            return;

        isRunning = true;
        mBluetoothListener.onConnected();

        Log.d(TAG, "Communication Thread is running...");

        // Keep listening to the InputStream until an exception occurs.
        while (isRunning) {
            try {
                byte[] buffer = new byte[1024];
                int numBytes;
                while ((numBytes = connectedInputStream.read(buffer)) != -1) {
                    byte[] bytes = Arrays.copyOf(buffer, numBytes);
                    mBluetoothListener.onReceive(bytes);
                }
                Log.d(TAG, "Full message decoded. " + numBytes);
            } catch (IOException e) {
                Log.e(TAG, "Input stream was disconnected.");
                isRunning = false;
                cancel();
            }
        }

    }

    public void write(byte[] bytes) {
        try {
            connectedOutputStream.write(bytes);
        } catch (IOException e) {
            Log.e(TAG, "Failed to send data.");
            cancel();
        }
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel() {
        Log.d(TAG, "Canceling communications thread.");
        isRunning = false;
        if (connectedInputStream != null) {
            try {
                connectedInputStream.close();
                connectedInputStream = null;
                Log.d(TAG, "Closed Input Stream successfully.");
            } catch (IOException e) {
                Log.e(TAG, "Failed to close Input Stream.", e);
            }
        }
        if (connectedOutputStream != null) {
            try {
                connectedOutputStream.close();
                connectedOutputStream = null;
                Log.d(TAG, "Closed Output Stream successfully.");
            } catch (IOException e) {
                Log.e(TAG, "Failed to close Output Stream.", e);
            }
        }
        if (mBluetoothSocket != null) {
            try {
                mBluetoothSocket.close();
                mBluetoothSocket = null;
                Log.d(TAG, "Closed Client Socket successfully.");
            } catch (IOException e) {
                Log.e(TAG, "Failed to close Client Socket.", e);
            }
        }
        mBluetoothListener.onDisconnected();
    }

    public void setBluetoothEventsListener(BluetoothEventsListener bluetoothEventsListener) {
        mBluetoothListener = bluetoothEventsListener;
    }

}
