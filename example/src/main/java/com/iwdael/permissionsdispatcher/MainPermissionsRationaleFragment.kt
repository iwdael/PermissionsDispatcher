package com.iwdael.permissionsdispatcher

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.iwdael.permissionsdispatcher.annotation.*

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
@PermissionsDispatcher
class MainPermissionsRationaleFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_main, container, false)
        view.findViewById<View>(R.id.btnCamera).setOnClickListener {
            takePhotoWithPermission("", "")
        }
        return view
    }

    @PermissionsDispatcherNeeds(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE)
    fun takePhoto(path: String, name: String) {
        Log.v("dzq", "takePhoto")
    }

    @PermissionsDispatcherDenied(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE)
    fun takePhotoDenied(permission: List<String>, bannedResults: List<Boolean>) {
        Log.v("dzq","takePhotoDenied:[${permission.joinToString()}],[${bannedResults.joinToString()}]")
    }


    @PermissionsDispatcherRationale(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE)
    fun takePhotoRationale(rationale: PermissionsRationale) {
        Log.v("dzq", "takePhotoRationale")
        rationale.apply()
    }
}