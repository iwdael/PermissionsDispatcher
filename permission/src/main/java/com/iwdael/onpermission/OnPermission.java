package com.iwdael.onpermission;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;


import androidx.core.content.ContextCompat;

import com.iwdael.onpermission.proxy.ProxyFragment;

import java.util.ArrayList;

import java.util.List;

/**
 * author  : Iwdael
 * e-mail  : iwdael@outlook.com
 * github  : http://github.com/iwdael
 * project : OnPermission
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

    public static void grant(Activity activity, Permission permission) {
        new OnPermission(activity).grant(permission);
    }

    public void grant(Permission permission) {
        String[] permissions = checkPermission(activity, permission);
        if (permissions.length == 0) return;
        getFragment().setPermission(permission);
        getFragment().requestPermissions(permissions, ProxyFragment.requestcode);
    }

    public static String[] checkPermission(Context context, Permission permission) {
        List<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permission.onGranted(permission.permissions());
            return permissions.toArray(new String[permissions.size()]);
        }
        for (String s : permission.permissions()) {
            if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(context, s))
                permissions.add(s);
        }
        if (permissions.size() == 0) permission.onGranted(permission.permissions());
        return permissions.toArray(new String[permissions.size()]);
    }
}
