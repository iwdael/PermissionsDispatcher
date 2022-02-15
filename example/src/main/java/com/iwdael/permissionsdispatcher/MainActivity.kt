package com.iwdael.permissionsdispatcher

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.iwdael.permissionsdispatcher.annotation.*
/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
@PermissionsDispatcher
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        findViewById<View>(R.id.btnCamera).setOnClickListener { takePhotoWithPermission("", "") }
        supportFragmentManager.beginTransaction().add(android.R.id.content,MainFragment()).commitNow()
    }

    @PermissionsDispatcherNeeds(Manifest.permission.CAMERA)
    fun takePhoto(path: String, name: String) {
        Log.v("dzq", "takePhoto")
    }

    @PermissionsDispatcherDenied(Manifest.permission.CAMERA)
    fun takePhotoDenied(permission: List<String>, bannedResults: List<Boolean>) {
        Log.v("dzq", "takePhotoDenied:[${permission.joinToString()}] , [${bannedResults.joinToString()}]")
    }

    @PermissionsDispatcherRationale(Manifest.permission.CAMERA)
    fun takePhotoRationale(rationale: PermissionsRationale) {
        Log.v("dzq", "takePhotoRationale")
        rationale.apply()
    }

}