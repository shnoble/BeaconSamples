package com.daya.android.ble.sample;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.daya.android.ble.sample.ble.BleManager;
import com.daya.android.ble.sample.ble.BleScanCallback;
import com.daya.android.ble.sample.databinding.ActivityMainBinding;
import com.daya.android.ble.sample.permisson.LocationPermissions;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BleScan";
    private static final int REQUEST_ENABLE_BT = 100;
    private BleManager mBleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBleManager = new BleManager(this, new BleScanCallback() {
            @Override
            public void onBleScan(@NonNull BluetoothDevice device, int rssi, @Nullable byte[] scanRecord) {
                Log.d(TAG, String.format("device: %s, rssi: %d, scanRecord: %s", device, rssi, Arrays.toString(scanRecord)));
            }
        });

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setBleManager(mBleManager);

        if (!BleManager.isSupported(this)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mBleManager.requestEnableBluetoothIfNot(this, REQUEST_ENABLE_BT,
                new BleManager.RequestEnableBluetoothResultCallback() {

                    @Override
                    public void onBluetoothEnabled() {
                        Toast.makeText(MainActivity.this, "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
                        requestPermissions();
                    }

                    @Override
                    public void onBluetoothCanceled() {
                        Toast.makeText(MainActivity.this, "Bluetooth Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void requestPermissions() {
        LocationPermissions.requestPermissions(this,
                new LocationPermissions.RequestPermissionsResultCallback() {
                    @Override
                    public void onPermissionsGranted(@NonNull List<String> permissions) {
                        Toast.makeText(MainActivity.this, "onPermissionsGranted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionsDenied(@NonNull List<String> permissions) {
                        Toast.makeText(MainActivity.this, "onPermissionsDenied", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        LocationPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mBleManager.onActivityResult(requestCode, resultCode, data);
    }
}
