package edu.gatech.ic.hudvswatch.shared;

/**
 * Created by p13i on 10/31/18.
 */

public final class Shared {
    public static final class BLUETOOTH {

        /**
         * Encapsulates all information related to maintaining a Bluetooth session between devices.
         */
        public static final class RF_COMM_SERVICE_RECORD {
            /**
             * Used by {@see android.bluetooth.BluetoothDevice.createRfcommSocketToServiceRecord}
             * and {@see android.bluetooth.BluetoothAdapter.listenUsingRfcommWithServiceRecord} to
             * create a shared Bluetooth session between a client and server.
             */
            public final static java.util.UUID UUID = java.util.UUID.fromString("c0b90515-8492-4bbe-af7e-c4233be88524");

            public static final String NAME = RF_COMM_SERVICE_RECORD.class.getCanonicalName();

        }

        public static final class DEVICE_NAMES {
            public static final String MOBILE = "Samsung Galaxy Note 8";
            public static final String WATCH = "Moto 360 2LTS";
            public static final String HUD = "Google Glass";
        }

        public static final class MAC_ADDRESSES {
            // Samsung Galaxy Note 8
            public static final String MOBILE = "50:77:05:0F:6D:20";
            // Moto 360
            public static final String WATCH = "24:DA:9B:96:4F:CE";
            // Google Glass
            public static final String HUD = "F8:8F:CA:12:F8:9B";
        }

    }
}
