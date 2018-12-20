package com.daya.android.ble.sample.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class BleScanner {
    @NonNull
    private final Context mContext;

    @NonNull
    private final BleScanCallback mBleScanCallback;

    @Nullable
    private BluetoothAdapter mBluetoothAdapter;

    @Nullable
    private ExecutorService mExecutorService;

    BleScanner(@NonNull Context context, @NonNull BleScanCallback callback) {
        this.mContext = context;
        this.mBleScanCallback = callback;
    }

    static BleScanner newScanner(@NonNull Context context, @NonNull BleScanCallback callback) {
        return new BleScannerForOreo(context, callback);
    }

    abstract void startScan();

    abstract void stopScan();

    @NonNull
    BleScanCallback getBleScanCallback() {
        return mBleScanCallback;
    }

    @Nullable
    synchronized BluetoothAdapter getBluetoothAdapter() {
        try {
            if (mBluetoothAdapter == null) {
                // Initializes Bluetooth adapter.
                final BluetoothManager bluetoothManager =
                        (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
                if (bluetoothManager != null) {
                    mBluetoothAdapter = bluetoothManager.getAdapter();
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return mBluetoothAdapter;
    }

    @NonNull
    synchronized ExecutorService getExecutorService() {
        if (mExecutorService == null) {
            mExecutorService = Executors.newSingleThreadExecutor();
        }
        return mExecutorService;
    }
}
