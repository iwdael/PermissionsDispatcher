package com.iwdael.permissionsdispatcher

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher

data class PermissionLauncher(val activity: Activity, val launcher: ActivityResultLauncher<Array<String>>, val callback: PermissionResult)