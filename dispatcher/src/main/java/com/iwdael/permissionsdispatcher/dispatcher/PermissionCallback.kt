package com.iwdael.permissionsdispatcher.dispatcher

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
interface PermissionCallback {
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
}