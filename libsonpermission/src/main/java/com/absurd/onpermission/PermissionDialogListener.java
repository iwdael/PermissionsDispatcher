package com.absurd.onpermission;

/**
 * Author: mr-absurd
 * Github: http://github.com/mr-absurd
 * Data: 2017/9/28.
 */

public interface PermissionDialogListener {
    void onConfirm(String permission);

    void onCancel(String permission);
}
