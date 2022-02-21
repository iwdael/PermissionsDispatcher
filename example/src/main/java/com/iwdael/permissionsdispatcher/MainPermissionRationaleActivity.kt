package com.iwdael.permissionsdispatcher

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.iwdael.permissionsdispatcher.annotation.*

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
@PermissionsDispatcher
class MainPermissionRationaleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .add(android.R.id.content, MainPermissionsRationaleFragment())
            .commitNow()
    }


    @PermissionsDispatcherNeeds(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE ,Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun takePhoto(path: String, name: String) {
        Log.v("dzq", "takePhoto")
    }

    @PermissionsDispatcherDenied(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE ,Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )
    fun takePhotoDenied(permission: List<String>, bannedResults: List<Boolean>) {
        Log.v(
            "dzq",
            "takePhotoDenied:[${permission.joinToString()}],[${bannedResults.joinToString()}]"
        )
    }
    @PermissionDispatcherRationale(
        Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
        target = Manifest.permission.CAMERA
    )
    fun cameraRationale(rationale: PermissionsRationale) {
        Log.v("dzq", "takePhotoRationale")
        rationale.apply()
    }

    @PermissionDispatcherRationale(
        Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
        target = Manifest.permission.READ_EXTERNAL_STORAGE
    )
    fun storageRationale(rationale: PermissionsRationale) {
        Log.v("dzq", "takePhotoRationale")
        rationale.apply()
    }

//    @PermissionsDispatcherRationale(
//        Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
//    )
//    fun allRationale(rationale: PermissionsRationale) {
//        Log.v("dzq", "takePhotoRationale")
//        rationale.apply()
//    }
}