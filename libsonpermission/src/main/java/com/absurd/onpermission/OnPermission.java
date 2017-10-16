package com.absurd.onpermission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Author: mr-absurd
 * Github: http://github.com/mr-absurd
 * Data: 2017/9/27.
 */

public class OnPermission implements PermissionDialogListener {
    private Activity mActivity;
    private PermissionListener mListener;
    private List<Permission> mNewPermission;
    private Handler mainHandler=new Handler(Looper.getMainLooper());
    public OnPermission(Activity mActivity) {
        this.mActivity = mActivity;
        mNewPermission = new ArrayList<>();
    }

    public void setPermissionListener(PermissionListener listener) {
        mListener = listener;
    }

    public void authorize(String[] permissions) {
        List<String> newPermission = new ArrayList<>();
        for (String permission : permissions) {
            if (checkPermission(permission)) {
                mListener.onAuthorize(permission, true);
            } else {
                newPermission.add(permission);
            }
        }
        if (newPermission.size() > 0) {
            authorizePermission(newPermission);
        }
    }


    public void authorize(Permission[] permissions) {
        for (Permission permission : permissions) {
            if (checkPermission(permission.getPermision())) {
                mListener.onAuthorize(permission.getPermision(), true);
            } else {
                OnPermissionDialog dialog = new OnPermissionDialog(mActivity, permission.getPermision(), permission.getContent());
                dialog.setPermissionDialogListener(this);
                dialog.show();
            }
        }
    }


    private boolean checkPermission(String permission) {
        boolean result;
        if (ContextCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
            result = false;
        } else {
            result = true;
        }
        return result;
    }

    private void authorizePermission(List<String> permissions) {
        mActivity.requestPermissions(permissions.toArray(new String[permissions.size()]), UsePermission.REQUESTCODE);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != UsePermission.REQUESTCODE) return;
        if (mListener == null) return;
        for (int i = 0; i < permissions.length; i++) {
            mListener.onAuthorize(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED ? true : false);
        }
    }

    @Override
    public void onConfirm(String permission) {
        List<String> permissions = new ArrayList<>();
        permissions.add(permission);
        authorizePermission(permissions);
    }

    @Override
    public void onCancel(final String permission) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onAuthorize(permission, false);
            }
        });

    }
}
