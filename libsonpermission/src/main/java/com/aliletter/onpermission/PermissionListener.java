package com.aliletter.onpermission;

import java.util.List;

/**
 * Author: aliletter
 * Github: http://github.com/aliletter
 * Data: 2017/9/27.
 */

public interface PermissionListener {
    void onAuthorize( String  permission, boolean result);
}
