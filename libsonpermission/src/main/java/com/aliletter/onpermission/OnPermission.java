package com.aliletter.onpermission;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Build;



import android.support.v4.content.ContextCompat;


import com.aliletter.onpermission.proxy.ProxyFragment;

import java.util.ArrayList;

import java.util.List;

/**
 * Author: aliletter
 * Github: http://github.com/aliletter
 * Data: 2017/9/27.
 */

public class OnPermission {
    protected Activity activity;
    protected final String TAG = ProxyFragment.class.getName();

    public OnPermission(Activity activity) {
        this.activity = activity;
    }

    private ProxyFragment getFragment() {
        Fragment fragment = activity.getFragmentManager().findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new ProxyFragment();
            FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(fragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return (ProxyFragment) fragment;
    }

    public void grant(Permission permission) {
        String[] permissions = checkPermisson(permission);
        if (permissions.length == 0) return;
        getFragment().setPermission(permission);
        getFragment().requestPermissions(permissions, ProxyFragment.requestcode);
    }

    private String[] checkPermisson(Permission permission) {
        List<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permission.onGranted(permission.permissions());
            return permissions.toArray(new String[permissions.size()]);
        }
        for (String s : permission.permissions()) {
            if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(getFragment().getContext(), s))
                permissions.add(s);
        }
        if (permissions.size() == 0) permission.onGranted(permission.permissions());
        return permissions.toArray(new String[permissions.size()]);
    }
}
