package com.iwdael.permissionsdispatcher

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.iwdael.permissionsdispatcher.annotation.*
import com.iwdael.permissionsdispatcher.dispatcher.PermissionsRationale

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
@PermissionsDispatcher
class MainPermissionsRationaleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction().add(android.R.id.content, MainPermissionsRationaleFragment())
            .commitNow()
    }

    @PermissionsDispatcherNeeds(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE)
    fun takePhoto(path: String, name: String) {
        Log.v("dzq", "takePhoto")
    }

//    @PermissionsDispatcherDenied(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE)
//    fun takePhotoDenied(permission: List<String>, bannedResults: List<Boolean>) {
//        Log.v("dzq","takePhotoDenied:[${permission.joinToString()}],[${bannedResults.joinToString()}]")
//    }


//    @PermissionsDispatcherRationale(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE)
//    fun takePhotoRationale(rationale: PermissionsRationale) {
//        Log.v("dzq", "takePhotoRationale")
//        rationale.apply()
//    }

}