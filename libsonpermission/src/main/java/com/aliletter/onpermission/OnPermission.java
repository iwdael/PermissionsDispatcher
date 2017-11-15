package com.aliletter.onpermission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: aliletter
 * Github: http://github.com/aliletter
 * Data: 2017/9/27.
 */

public class OnPermission implements PermissionDialogListener {
    private Activity mActivity;
    private PermissionListener mListener;
    private List<Permission> mNewPermission;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public OnPermission(Activity mActivity) {
        this.mActivity = mActivity;
        mNewPermission = new ArrayList<>();
    }

    public void setPermissionListener(PermissionListener listener) {
        mListener = listener;
    }

    public void authorize(String[] permissions) {

        for (String permission : permissions) {
            if (checkPermission(permission)) {
                mListener.onAuthorize(permission, true);
            } else {
                mNewPermission.add(new Permission(permission, null));
            }
        }
        if (mNewPermission.size() > 0) {
            List<String> list = new ArrayList();
            for (Permission permission : mNewPermission) {
                list.add(permission.getPermision());
            }
            authorizePermission(list);
            mNewPermission.clear();
        }
    }

    public void authorize(String[] permissions, String content) {

        for (String permission : permissions) {
            if (checkPermission(permission)) {
                mListener.onAuthorize(permission, true);
            } else {
                mNewPermission.add(new Permission(permission, null));
            }
        }
        if (mNewPermission.size() > 0) {
            List<String> list = new ArrayList();
            for (Permission permission : mNewPermission) {
                list.add(permission.getPermision());
            }
            OnPermissionDialog dialog = new OnPermissionDialog(mActivity, list, mNewPermission.get(0).getContent());
            dialog.setPermissionDialogListener(this);
            dialog.show();
            mNewPermission.clear();
        }

    }

    public void authorize(Permission[] permissions) {
        for (Permission permission : permissions) {
            if (checkPermission(permission.getPermision())) {
                mListener.onAuthorize(permission.getPermision(), true);
            } else {
                mNewPermission.add(permission);
            }
        }
        if (mNewPermission.size() > 0) {
            List<String> list = new ArrayList();
            list.add(mNewPermission.get(0).getPermision());
            OnPermissionDialog dialog = new OnPermissionDialog(mActivity, list, mNewPermission.get(0).getContent());
            dialog.setPermissionDialogListener(this);
            dialog.show();
            mNewPermission.remove(0);
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
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED ? true : false) {
                if (mNewPermission.size() > 0) {
                    List<String> list = new ArrayList<>();
                    list.add(mNewPermission.get(0).getPermision());
                    OnPermissionDialog dialog = new OnPermissionDialog(mActivity, list, mNewPermission.get(0).getContent());
                    dialog.setPermissionDialogListener(this);
                    dialog.show();
                    mNewPermission.remove(0);
                }
            }
        }
    }

    @Override
    public void onConfirm(List<String> permission) {

        authorizePermission(permission);

    }

    @Override
    public void onCancel(final List<String> permission) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                for (String s : permission) {
                    mListener.onAuthorize(s, false);
                }

            }
        });

    }
}
