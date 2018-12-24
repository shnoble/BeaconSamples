package com.daya.android.ble.sample;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
    private BluetoothDevice mBluetoothDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBleManager = new BleManager(this, new BleScanCallback() {
            @Override
            public void onBleScan(@NonNull BluetoothDevice device, int rssi, @Nullable byte[] scanRecord) {
                if ("MiniBeacon_96462".equalsIgnoreCase(device.getName())) {
                    Log.d(TAG, String.format("name: %s, device: %s, rssi: %d, scanRecord: %s", device.getName(), device, rssi, Arrays.toString(scanRecord)));
                    Log.d(TAG, "scanRecord length: " + (scanRecord != null ? scanRecord.length : 0));
                    if (scanRecord != null) {
                        // AD flags.
                        int advertisingFlagsDataLength = scanRecord[0] & 0xff;
                        Log.d(TAG, "Length of advertising flags data: " + advertisingFlagsDataLength);

                        byte advertisingType = scanRecord[1];
                        byte advertisingStatus = scanRecord[2];

                        /*
                            bit 0 LE Limited Discoverable Mode
                            bit 1 LE General Discoverable Mode
                            bit 2 BR/EDR Not Supported
                            bit 3 Simultaneous LE and BR/EDR to Same Device Capable (controller)
                            bit 4 Simultaneous LE and BR/EDR to Same Device Capable (Host)
                            bit 5~7 Reserved
                        */
                        boolean leLimitedDiscoverableMode = (advertisingStatus & 0x01) == 0x01;
                        boolean leGeneralDiscoverableMode = (advertisingStatus & 0x02) == 0x02;
                        boolean brEdrNotSupported = (advertisingStatus & 0x04) == 0x04;
                        boolean simultaneousLeAndBrEdrController = (advertisingStatus & 0x08) == 0x08;
                        boolean simultaneousLeAndBrEdrHost = (advertisingStatus & 0x10) == 0x10;

                        Log.d(TAG, String.format("Advertising type flags: 0x%x", advertisingType));
                        Log.d(TAG, String.format("Advertising status flags: 0x%x", advertisingStatus));
                        Log.d(TAG, "LE Limited Discoverable Mode: " + leLimitedDiscoverableMode);
                        Log.d(TAG, "LE General Discoverable Mode: " + leGeneralDiscoverableMode);
                        Log.d(TAG, "BR/EDR Not Supported: " + brEdrNotSupported);
                        Log.d(TAG, "Simultaneous LE and BR/EDR to Same Device Capable (controller): " + simultaneousLeAndBrEdrController);
                        Log.d(TAG, "Simultaneous LE and BR/EDR to Same Device Capable (Host): " + simultaneousLeAndBrEdrHost);

                        // AD headers
                        int advertisingHeaderDataLength = scanRecord[3] & 0xff;
                        byte manufacturerSpecificDataFlag = scanRecord[4];
                        int companyId = ((scanRecord[6] & 0xff ) << 8) | (scanRecord[5] & 0xff);
                        byte typeId = scanRecord[7];
                        Log.d(TAG, "Length of advertising header data: " + advertisingHeaderDataLength);
                        Log.d(TAG, String.format("Manufacturer-Specific Data Flag: 0x%x", manufacturerSpecificDataFlag));
                        Log.d(TAG, "Company ID: " + companyId);
                        Log.d(TAG, String.format("Type ID: 0x%x", typeId));

                        // AD indicator
                        int advertisingIndicatorDataLength = scanRecord[9] & 0xff;
                        Log.d(TAG, "Advertising indicator data length: " + advertisingIndicatorDataLength);
                        byte[] uuid = Arrays.copyOfRange(scanRecord, 9, 25);
                        Log.d(TAG, "UUID length: " + uuid.length);
                        Log.d(TAG, "UUID: " + Arrays.toString(uuid));
                        int majorNumber = ((scanRecord[25] & 0xff ) << 8) | (scanRecord[26] & 0xff);
                        Log.d(TAG, "Major number: " + majorNumber);
                        int minorNumber = ((scanRecord[27] & 0xff ) << 8) | (scanRecord[28] & 0xff);
                        Log.d(TAG, "Minor number: " + minorNumber);
                        int txPower = scanRecord[29] & 0xff;
                        Log.d(TAG, "TX Power: " + txPower);
                    }
                    mBluetoothDevice = device;
                }
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

    public void onConnect(View view) {
        if (mBluetoothDevice == null) {
            return;
        }

        /*final Intent intent = new Intent(this, DeviceControlActivity.class);
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, mBluetoothDevice.getName());
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, mBluetoothDevice.getAddress());
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        startActivity(intent);*/
    }
}
