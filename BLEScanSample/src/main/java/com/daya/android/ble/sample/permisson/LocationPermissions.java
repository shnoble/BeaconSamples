package com.daya.android.ble.sample.permisson;

import android.Manifest;
import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.List;

public class LocationPermissions {
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 100;

    private static DayaPermissions sPermissions;
    private static RequestPermissionsResultCallback sRequestPermissionsResultCallback;

    public static void requestPermissions(@NonNull final Activity activity,
                                          @NonNull RequestPermissionsResultCallback callback) {
        getPermissionsHelper().setRequestPermissionsResultCallback(
                new DayaPermissions.RequestPermissionsResultCallback() {
                    @Override
                    public void onPermissionsGranted(int requestCode, @NonNull List<String> permissions) {
                        if (requestCode == PERMISSION_REQUEST_COARSE_LOCATION) {
                            if (sRequestPermissionsResultCallback != null) {
                                sRequestPermissionsResultCallback.onPermissionsGranted(permissions);
                            }
                        }
                    }

                    @Override
                    public void onPermissionsDenied(int requestCode, @NonNull List<String> permissions) {
                        if (requestCode == PERMISSION_REQUEST_COARSE_LOCATION) {
                            if (sRequestPermissionsResultCallback != null) {
                                sRequestPermissionsResultCallback.onPermissionsGranted(permissions);
                            }
                        }
                    }
                });

        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION};
        PermissionRequest.Builder permissionRequestBuilder =
                PermissionRequest.newBuilder(permissions, PERMISSION_REQUEST_COARSE_LOCATION);
        sRequestPermissionsResultCallback = callback;
        getPermissionsHelper().requestPermissions(activity, permissionRequestBuilder.build());
    }

    public static void onRequestPermissionsResult(int requestCode,
                                                  @NonNull String[] permissions,
                                                  @NonNull int[] grantResults) {
        getPermissionsHelper().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static DayaPermissions getPermissionsHelper() {
        if (sPermissions == null) {
            sPermissions = new DayaPermissions();
        }
        return sPermissions;
    }

    public interface RequestPermissionsResultCallback {
        void onPermissionsGranted(@NonNull List<String> permissions);

        void onPermissionsDenied(@NonNull List<String> permissions);
    }
}
