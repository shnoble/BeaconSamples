package com.daya.android.ble.sample.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class BleManager {
    @NonNull
    private final BleScanner mBleScanner;

    private int mRequestEnableBluetoothCode;

    @Nullable
    private RequestEnableBluetoothResultCallback mRequestEnableBluetoothResultCallback;

    public BleManager(@NonNull Context context, @NonNull BleScanCallback callback) {
        this.mBleScanner = BleScanner.newScanner(context, callback);
    }

    public static boolean isSupported(@NonNull Context context) {
        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        }
        return false;
    }

    @Nullable
    private static BluetoothAdapter getBluetoothAdapter(@NonNull Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // Initializes Bluetooth adapter.
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager != null) {
                return bluetoothManager.getAdapter();
            }
        }
        return null;
    }

    public void startScan() {
        mBleScanner.startScan();
    }

    public void stopScan() {
        mBleScanner.stopScan();
    }

    public interface RequestEnableBluetoothResultCallback {
        void onBluetoothEnabled();

        void onBluetoothCanceled();
    }

    public void requestEnableBluetoothIfNot(@NonNull Activity activity,
                                            @IntRange(from = 1) int requestCode,
                                            @NonNull RequestEnableBluetoothResultCallback callback) {
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter(activity);
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mRequestEnableBluetoothCode = requestCode;
            mRequestEnableBluetoothResultCallback = callback;
            activity.startActivityForResult(enableBtIntent, requestCode);
        } else {
            callback.onBluetoothEnabled();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == mRequestEnableBluetoothCode
                && mRequestEnableBluetoothResultCallback != null) {
            if (resultCode == Activity.RESULT_CANCELED) {
                mRequestEnableBluetoothResultCallback.onBluetoothCanceled();
            } else {
                mRequestEnableBluetoothResultCallback.onBluetoothEnabled();
            }

        }
    }
}
