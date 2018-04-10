package com.blackchopper.onpermission;

/**
 * author  : Black Chopper
 * e-mail  : 4884280@qq.com
 * github  : http://github.com/BlackChopper
 * project : OnPermission
 */
public interface Permission {
    String[] permissions();

    void onGranted(String[] permission);

    void onDenied(String[] permission);
}
