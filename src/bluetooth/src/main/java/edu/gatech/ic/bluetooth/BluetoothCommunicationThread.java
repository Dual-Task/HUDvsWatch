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

    private BluetoothEventsListener mBluetoothEventsListener;

    private BluetoothSocket mBluetoothSocket;

    private InputStream mInputStream;
    private OutputStream mOutputStream;

    private boolean mIsRunning;

    public BluetoothCommunicationThread(BluetoothSocket mBluetoothSocket) {
        this.mBluetoothSocket = mBluetoothSocket;

        // Connect to socket
        try {
            mInputStream = new DataInputStream(mBluetoothSocket.getInputStream());
            mOutputStream = new DataOutputStream(mBluetoothSocket.getOutputStream());
            Log.d(TAG, "I/O Streams created.");
        } catch (IOException e) {
            Log.e(TAG, "Failed to create the I/O streams.", e);
            cancel();
        }
    }

    public void run() {

        mIsRunning = true;
        mBluetoothEventsListener.onConnected();

        Log.d(TAG, "Communication Thread is running...");

        // Keep listening to the InputStream until an exception occurs.
        while (mIsRunning) {
            try {
                byte[] buffer = new byte[1024];
                int numBytes;
                while ((numBytes = mInputStream.read(buffer)) != -1) {
                    byte[] bytes = Arrays.copyOf(buffer, numBytes);
                    mBluetoothEventsListener.onReceive(bytes);
                }
                Log.d(TAG, "Full message decoded with " + numBytes + " bytes.");
            } catch (IOException e) {
                Log.e(TAG, "Input stream was disconnected.");
                mIsRunning = false;
                cancel();
            }
        }

    }

    public void write(byte[] bytes) {
        try {
            mOutputStream.write(bytes);
        } catch (IOException e) {
            Log.e(TAG, "Failed to send data.");
            cancel();
        }
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel() {
        Log.d(TAG, "Canceling communications thread.");
        mIsRunning = false;
        if (mInputStream != null) {
            try {
                mInputStream.close();
                mInputStream = null;
                Log.d(TAG, "Closed Input Stream successfully.");
            } catch (IOException e) {
                Log.e(TAG, "Failed to close Input Stream.", e);
            }
        }
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
                mOutputStream = null;
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
        mBluetoothEventsListener.onDisconnected();
    }

    public void setBluetoothEventsListener(BluetoothEventsListener bluetoothEventsListener) {
        mBluetoothEventsListener = bluetoothEventsListener;
    }

}
