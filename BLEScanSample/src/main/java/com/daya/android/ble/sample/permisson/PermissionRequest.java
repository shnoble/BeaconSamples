package com.daya.android.ble.sample.permisson;

import android.support.annotation.NonNull;
import android.support.annotation.Size;

public class PermissionRequest {
    private final String[] mPermissions;
    private int mRequestCode;

    private PermissionRequest(@NonNull @Size(min = 1) String[] permissions, int requestCode) {
        this.mPermissions = permissions;
        this.mRequestCode = requestCode;
    }

    String[] getPermissions() {
        return mPermissions;
    }

    int getRequestCode() {
        return mRequestCode;
    }

    public static Builder newBuilder(@NonNull @Size(min = 1) String[] permissions,
                                     int requestCode) {
        return new Builder(permissions, requestCode);
    }

    public static class Builder {
        private final String[] mPermissions;
        private final int mRequestCode;

        private Builder(@NonNull @Size(min = 1) String[] permissions, int requestCode) {
            this.mPermissions = permissions;
            this.mRequestCode = requestCode;
        }

        public PermissionRequest build() {
            return new PermissionRequest(mPermissions, mRequestCode);
        }
    }
}
