package com.blackchopper.onpermission.proxy;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;


import com.blackchopper.onpermission.Permission;

import java.util.ArrayList;
import java.util.List;

/**
 * author  : Black Chopper
 * e-mail  : 4884280@qq.com
 * github  : http://github.com/BlackChopper
 * project : OnPermission
 */
public class ProxyFragment extends Fragment {
    public final static int requestcode = 10012;
    private Permission permission;

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == requestcode) {
            List<String> deniedPermisson = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    deniedPermisson.add(permissions[i]);
            }
            if (deniedPermisson.size() == 0)
                permission.onGranted(permission.permissions());
            else
                permission.onDenied(deniedPermisson.toArray(new String[deniedPermisson.size()]));
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
