package com.iwdael.permissionsdispatcher

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
private val MIN_SDK_PERMISSIONS = mapOf(
    "com.android.voicemail.permission.ADD_VOICEMAIL" to 14,
    "android.permission.READ_CALL_LOG" to 16,
    "android.permission.READ_EXTERNAL_STORAGE" to 16,
    "android.permission.WRITE_CALL_LOG" to 16,
    "android.permission.BODY_SENSORS" to 20,
    "android.permission.SYSTEM_ALERT_WINDOW" to 23,
    "android.permission.WRITE_SETTINGS" to 23,
    "android.permission.READ_PHONE_NUMBERS" to 26,
    "android.permission.ANSWER_PHONE_CALLS" to 26,
    "android.permission.ACCEPT_HANDOVER" to 28,
    "android.permission.ACTIVITY_RECOGNITION" to 29,
    "android.permission.ACCESS_MEDIA_LOCATION" to 29,
    "android.permission.ACCESS_BACKGROUND_LOCATION" to 29
)

/**
 * hasPermissions(vararg permissions: String)
 */
private fun Context.hasPermission(permission: String): Boolean {
    return try {
        PermissionChecker.checkSelfPermission(
            this,
            permission
        ) == PermissionChecker.PERMISSION_GRANTED
    } catch (t: RuntimeException) {
        false
    }
}

private fun Context.hasPermissions(vararg permissions: String) = !permissions.any { !hasPermission(it) }
private fun Fragment.hasPermissions(vararg permissions: String) = context?.hasPermissions(*permissions) == true
private fun hasPermissions(vararg permissions: String) = currentActivity?.hasPermissions(*permissions) == true


/**
 * checkRequestPermission
 */
fun Context.checkRequestPermission(vararg permissions: Permission) = permissions.filter { Build.VERSION.SDK_INT in it.min..it.max }.map { it.value }.filter { !hasPermissions(it) }.toTypedArray()
fun Fragment.checkRequestPermission(vararg permissions: Permission): Array<String> {
    return context?.checkRequestPermission(*permissions) ?: (permissions.toList().map { it.value }.toTypedArray())
}

fun checkRequestPermission(vararg permissions: Permission) = currentActivity?.checkRequestPermission(*permissions) ?: (permissions.map { it.value }.toList().toTypedArray())


/**
 * showRequestPermissionRationale
 */
fun Activity.showRequestPermissionRationale(permission: String): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        shouldShowRequestPermissionRationale(permission)
    } else {
        true
    }
}

fun Fragment.showRequestPermissionRationale(permission: String): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        shouldShowRequestPermissionRationale(permission)
    } else {
        true
    }
}

fun showRequestPermissionRationale(permission: String) = currentActivity?.showRequestPermissionRationale(permission) == true


fun ComponentActivity.registerPermissionLauncher(listener: PermissionListener) = permissionActivityLifecycle.createActivityLauncher(this)
    .apply {
        if (this == null) throw IllegalStateException("Activity ${this@registerPermissionLauncher} not support")
        callback.registerPermissionListener(listener)
    }
    .let { it!!.launcher }


fun registerPermissionLauncher(listener: PermissionListener): ActivityResultLauncher<Array<String>> {
    val activity = currentActivity
    if (activity is ComponentActivity) {
        return activity.registerPermissionLauncher(listener)
    } else {
        throw RuntimeException("not found androidx.activity.ComponentActivity")
    }
}

internal val currentActivity: Activity?
    get() {
        return permissionActivityLifecycle.activities.firstOrNull()?.activity
    }

val permissionActivityLifecycle = PermissionActivityLifecycle()