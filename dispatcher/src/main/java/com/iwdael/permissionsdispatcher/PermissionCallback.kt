package com.iwdael.permissionsdispatcher

import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class PermissionCallback : ActivityResultCallback<Map<String, Boolean>> {
    private val listeners = mutableListOf<PermissionListener>()

    override fun onActivityResult(result: Map<String, Boolean>) {
        listeners.forEach { it.onPermissionResult(result) }
        listeners.clear()
    }

    fun registerPermissionListener(listener: PermissionListener) {
        this.listeners.add(listener)
    }
}

interface PermissionListener {
    fun onPermissionResult(result: Map<String, Boolean>)
}