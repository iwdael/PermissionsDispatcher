package com.aliletter.onpermission;

import java.util.List;

/**
 * Author: aliletter
 * Github: http://github.com/aliletter
 * Data: 2017/9/28.
 */

public interface PermissionDialogListener {
    void onConfirm(List<String> permission);

    void onCancel(List<String>  permission);
}
