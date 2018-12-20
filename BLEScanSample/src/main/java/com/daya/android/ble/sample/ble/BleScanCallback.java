package com.daya.android.ble.sample.ble;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface BleScanCallback {
    void onBleScan(@NonNull BluetoothDevice device, int rssi, @Nullable byte[] scanRecord);
}
