package com.daya.android.ble.sample.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

@TargetApi(Build.VERSION_CODES.O)
class BleScannerForOreo extends BleScanner {
    @Nullable
    private BluetoothLeScanner mBluetoothLeScanner;

    @Nullable
    private ScanCallback mScanCallback;

    BleScannerForOreo(@NonNull Context context, @NonNull BleScanCallback callback) {
        super(context, callback);
    }

    @Override
    void startScan() {
        final BluetoothLeScanner bluetoothLeScanner = getBluetoothLeScanner();
        if (bluetoothLeScanner == null) {
            return;
        }

        final ScanCallback scanCallback = getScanCallback();
        getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                bluetoothLeScanner.startScan(scanCallback);
            }
        });
    }

    @Override
    void stopScan() {
        final BluetoothLeScanner bluetoothLeScanner = getBluetoothLeScanner();
        if (bluetoothLeScanner == null) {
            return;
        }

        final ScanCallback scanCallback = getScanCallback();
        getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                bluetoothLeScanner.stopScan(scanCallback);
            }
        });
    }

    @Nullable
    private BluetoothLeScanner getBluetoothLeScanner() {
        try {
            if (mBluetoothLeScanner == null) {
                BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
                if (bluetoothAdapter != null) {
                    mBluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return mBluetoothLeScanner;
    }

    private ScanCallback getScanCallback() {
        if (mScanCallback == null) {
            mScanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);

                    byte[] scanRecord =
                            result.getScanRecord() != null ? result.getScanRecord().getBytes() : null;
                    getBleScanCallback().onBleScan(result.getDevice(), result.getRssi(), scanRecord);
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);

                    for (ScanResult result : results) {
                        byte[] scanRecord =
                                result.getScanRecord() != null ? result.getScanRecord().getBytes() : null;
                        getBleScanCallback().onBleScan(result.getDevice(), result.getRssi(), scanRecord);
                    }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                }
            };
        }
        return mScanCallback;
    }
}
