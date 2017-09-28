package com.iwdael.permissionsdispatcher

import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
abstract class PermissionCallback : ActivityResultCallback<Map<String, Boolean>> {
    private var permissionLauncher: ActivityResultLauncher<Array<String>>? = null
    fun attachPermissionLauncher(launcher: ActivityResultLauncher<Array<String>>) {
        permissionLauncher = launcher
    }

    override fun onActivityResult(result: Map<String, Boolean>) {
        permissionLauncher?.unregister()
        onPermissionResult(result)
    }

    abstract fun onPermissionResult(result: Map<String, Boolean>)
}