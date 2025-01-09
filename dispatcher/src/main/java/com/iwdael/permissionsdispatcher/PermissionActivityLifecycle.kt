package com.iwdael.permissionsdispatcher

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class PermissionActivityLifecycle : Application.ActivityLifecycleCallbacks {
    val activities = mutableListOf<PermissionLauncher>()
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        createActivityLauncher(activity)?.let {
            activities.add(0, it)
        }

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        activities.removeAll { it.activity == activity }
    }

    fun createActivityLauncher(activity: Activity): PermissionLauncher? {
        if (activity !is ComponentActivity) return null
        val activityLauncher = activities.firstOrNull { it.activity == activity }
        if (activityLauncher != null) return activityLauncher
        val callback = PermissionResult()
        val launcher = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(), callback)
        return PermissionLauncher(activity, launcher, callback)
    }
}