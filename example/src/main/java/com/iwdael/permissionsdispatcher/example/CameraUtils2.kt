package com.iwdael.permissionsdispatcher.example

import android.util.Log
import com.iwdael.permissionsdispatcher.annotation.Permission
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcher
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherDenied
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherNeeds
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherRationale
import com.iwdael.permissionsdispatcher.annotation.PermissionsRationale

/**
 * @author iwdael
 * @since 2024/8/22
 * @desc this is CameraUtils
 */
@PermissionsDispatcher
class CameraUtils2 {
    @PermissionsDispatcherNeeds(
        value = [
            Permission(value = android.Manifest.permission.CAMERA),
            Permission(value = android.Manifest.permission.ACCESS_FINE_LOCATION),
            Permission(value = android.Manifest.permission.WRITE_CALENDAR)
        ],
        identity = 1
    )
    fun captureCamera(width: Int, height: Int, paths: List<List<List<List<String?>?>?>?>?) {
        Log.v("dzq", "captureCamera ")
    }

    @PermissionsDispatcherDenied(
        value = [
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.WRITE_CALENDAR
        ],
        identity = 1
    )
    fun denyCaptureCamera(result: Map<String, Boolean>) {
//        Log.v("dzq", "denyCaptureCamera permission:${deniedPermissions.joinToString()}\nbannedResults:${bannedResults.joinToString()}")
    }

    @PermissionsDispatcherRationale(
        value = [
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.WRITE_CALENDAR
        ],
        identity = 1
    )
    fun rationaleCamera(rationale: PermissionsRationale) {
        Log.v("dzq", "rationaleCamera")
        rationale.apply()
    }
}