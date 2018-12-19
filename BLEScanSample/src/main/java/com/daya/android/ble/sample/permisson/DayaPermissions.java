package com.daya.android.ble.sample.permisson;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DayaPermissions {
    private RequestPermissionsResultCallback mRequestPermissionsResultCallback;

    public boolean hasPermissions(@NonNull Context context,
                                  @NonNull @Size(min = 1) String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String permission : permissions) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void requestPermissions(@NonNull Activity activity,
                                   @NonNull PermissionRequest request) {
        requestPermissions(activity, request.getPermissions(), request.getRequestCode());
    }

    private void requestPermissions(@NonNull Activity activity,
                                    @NonNull @Size(min = 1) String[] permission,
                                    @IntRange(from = 1) int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || hasPermissions(activity, permission)) {
            if (mRequestPermissionsResultCallback != null) {
                mRequestPermissionsResultCallback.onPermissionsGranted(
                        requestCode, Arrays.asList(permission));
            }
        } else {
            activity.requestPermissions(permission, requestCode);
        }
    }

    public interface RequestPermissionsResultCallback {
        void onPermissionsGranted(int requestCode, @NonNull List<String> permissions);

        void onPermissionsDenied(int requestCode, @NonNull List<String> permissions);
    }

    public void setRequestPermissionsResultCallback(
            @NonNull RequestPermissionsResultCallback callback) {
        mRequestPermissionsResultCallback = callback;
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        List<String> granted = null;
        List<String> denied = null;
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                if (granted == null) {
                    granted = new ArrayList<>();
                }
                granted.add(permission);
            } else {
                if (denied == null) {
                    denied = new ArrayList<>();
                }
                denied.add(permission);
            }
        }

        if (mRequestPermissionsResultCallback != null) {
            if (granted != null && !granted.isEmpty()) {
                mRequestPermissionsResultCallback.onPermissionsGranted(requestCode, granted);
            }
            if (denied != null && !denied.isEmpty()) {
                mRequestPermissionsResultCallback.onPermissionsDenied(requestCode, denied);
            }
        }
    }
}
