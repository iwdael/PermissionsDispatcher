package com.iwdael.permissionsdispatcher

import android.Manifest
import android.util.Log
import com.iwdael.permissionsdispatcher.annotation.*
/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
@PermissionsDispatcher
class SampleClass {

    @PermissionsDispatcherNeeds(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
    fun takePhoto(path: String, name: String) {
        Log.v("dzq-class", "takePhoto")
    }

    @PermissionsDispatcherDenied(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
    fun takePhotoDenied(permission: List<String>, bannedResults: List<Boolean>) {
        Log.v("dzq-class","takePhotoDenied:[${permission.joinToString()}],[${bannedResults.joinToString()}]")
    }


    @PermissionsDispatcherRationale(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
    fun takePhotoRationale(rationale: PermissionsRationale) {
        Log.v("dzq-class", "takePhotoRationale")
        rationale.apply()
    }
}