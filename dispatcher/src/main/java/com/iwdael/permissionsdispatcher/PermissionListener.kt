package com.iwdael.permissionsdispatcher

interface PermissionListener {
    fun onPermissionResult(result: Map<String, Boolean>)
}