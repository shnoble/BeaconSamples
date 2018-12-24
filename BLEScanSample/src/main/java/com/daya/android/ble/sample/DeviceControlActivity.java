package com.daya.android.ble.sample;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.daya.android.ble.sample.databinding.ActivityDeviceControlBinding;

public class DeviceControlActivity extends AppCompatActivity {

    private DeviceStatus mDeviceStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);

        mDeviceStatus = new DeviceStatus();

        ActivityDeviceControlBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_device_control);
        binding.setDeviceStatus(mDeviceStatus);
    }
}
