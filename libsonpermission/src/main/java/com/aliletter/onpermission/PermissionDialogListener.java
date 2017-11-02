package com.aliletter.onpermission;

/**
 * Author: aliletter
 * Github: http://github.com/aliletter
 * Data: 2017/9/28.
 */

public interface PermissionDialogListener {
    void onConfirm(String permission);

    void onCancel(String permission);
}
