package com.aliletter.onpermission;

/**
 * Author: aliletter
 * Github: http://github.com/aliletter
 * Data: 2017/9/27.
 */

public interface PermissionListener {
    void onAuthorize(String permission, boolean result);
}
