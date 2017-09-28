package com.absurd.onpermission;

/**
 * Author: mr-absurd
 * Github: http://github.com/mr-absurd
 * Data: 2017/9/27.
 */

public interface PermissionListener {
    void onAuthorize(String permission, boolean result);
}
