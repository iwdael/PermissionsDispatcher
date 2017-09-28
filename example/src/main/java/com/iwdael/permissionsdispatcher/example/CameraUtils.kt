package com.iwdael.permissionsdispatcher.example

import android.util.Log
import com.iwdael.permissionsdispatcher.annotation.PermissionDispatcherRationale
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
class CameraUtils {
    @PermissionsDispatcherNeeds(
        value = [
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.WRITE_CALENDAR
        ],
        identity = 1
    )
    fun captureCamera(width: Int, height: Int, paths: List<List<List<List<String>>>>) {
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

    @PermissionDispatcherRationale(
        value = [
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.WRITE_CALENDAR
        ],
        target = android.Manifest.permission.CAMERA,
        identity = 1
    )
    fun rationaleCamera(rationale: PermissionsRationale) {
        Log.v("dzq","rationaleCamera")
        rationale.apply()
    }


    @PermissionDispatcherRationale(
        value = [
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.WRITE_CALENDAR
        ],
        target = android.Manifest.permission.ACCESS_FINE_LOCATION,
        identity = 1
    )
    fun rationaleLocation(rationale: PermissionsRationale) {
        Log.v("dzq","rationaleLocation")
        rationale.apply()
    }


    @PermissionDispatcherRationale(
        value = [
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.WRITE_CALENDAR
        ],
        target = android.Manifest.permission.WRITE_CALENDAR,
        identity = 1
    )
    fun rationaleCalendar(rationale: PermissionsRationale) {
        Log.v("dzq","rationaleCalendar")
        rationale.apply()
    }

}