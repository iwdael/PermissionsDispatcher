package com.iwdael.onpermission

import androidx.appcompat.app.AppCompatActivity

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 * time   : 2019/8/5
 * desc   : On
 * version: 1.0
 */

fun AppCompatActivity.grant(permission: Permission) {
    OnPermission(this).grant(permission)
}