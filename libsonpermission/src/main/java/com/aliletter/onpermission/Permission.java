package com.aliletter.onpermission;

/**
 * Author: aliletter
 * Github: http://github.com/aliletter
 * Data: 2017/9/28.
 */

public interface Permission {
    String[] permissions();

    void onGranted(String[] permission);

    void onDenied(String[] permission);
}
