package com.iwdael.onpermission;

/**
 * author  : Iwdael
 * e-mail  : iwdael@outlook.com
 * github  : http://github.com/iwdael
 * project : OnPermission
 */
public interface Permission {
    String[] permissions();

    void onGranted(String[] permission);

    void onDenied(String[] permission);
}
