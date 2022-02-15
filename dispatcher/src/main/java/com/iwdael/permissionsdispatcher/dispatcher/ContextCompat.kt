package com.iwdael.permissionsdispatcher.dispatcher

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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

fun Context.hasPermissions(vararg permissions: String): Boolean {
    for (permission in permissions) if (!hasPermission(permission)) return false
    return true
}

fun Fragment.hasPermissions(vararg permissions: String): Boolean {
    for (permission in permissions) if (context?.hasPermission(permission) != true) return false
    return true
}


fun AppCompatActivity.getPermissionFragment(): PermissionFragment? {
    var fragment: Fragment? =
        supportFragmentManager.findFragmentByTag(PermissionFragment::class.java.name)
    if (fragment == null) {
        fragment = PermissionFragment()
        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentManager
            .beginTransaction()
            .add(fragment, PermissionFragment::class.java.name)
            .commitAllowingStateLoss()
        fragmentManager.executePendingTransactions()
    }
    return fragment as PermissionFragment?
}

fun Fragment.getPermissionFragment(): PermissionFragment? {
    activity ?: return null
    var fragment: Fragment? =
        activity!!.supportFragmentManager.findFragmentByTag(PermissionFragment::class.java.name)
    if (fragment == null) {
        fragment = PermissionFragment()
        val fragmentManager: FragmentManager = activity!!.supportFragmentManager
        fragmentManager
            .beginTransaction()
            .add(fragment, PermissionFragment::class.java.name)
            .commitAllowingStateLoss()
        fragmentManager.executePendingTransactions()
    }
    return fragment as PermissionFragment
}

fun Context.checkRequestPermission(vararg permissions: String): Array<String> {
    val keepPermissions =
        permissions.filter { BuildConfig.VERSION_CODE >= (MIN_SDK_PERMISSIONS[permissions] ?: 0) }
    return keepPermissions.filter { !hasPermissions(it) }.toTypedArray()
}

fun Fragment.checkRequestPermission(vararg permissions: String): Array<String> {
    val keepPermissions =
        permissions.filter { BuildConfig.VERSION_CODE >= (MIN_SDK_PERMISSIONS[permissions] ?: 0) }
    return keepPermissions.filter { !hasPermissions(it) }.toTypedArray()
}

fun Activity.showRequestPermissionRationale(permission: String): Boolean {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        shouldShowRequestPermissionRationale(permission)
    } else {
        true
    }
}

fun Fragment.showRequestPermissionRationale(permission: String): Boolean {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        shouldShowRequestPermissionRationale(permission)
    } else {
        true
    }
}