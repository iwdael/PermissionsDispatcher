package com.iwdael.permissionsdispatcher.dispatcher

import androidx.fragment.app.Fragment

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class PermissionFragment : Fragment() {
    private var permissionCallback: PermissionCallback? = null
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        this.permissionCallback?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        this.permissionCallback = null
    }

    fun setPermissionCallback(callback: PermissionCallback?) {
        this.permissionCallback = callback
    }
}