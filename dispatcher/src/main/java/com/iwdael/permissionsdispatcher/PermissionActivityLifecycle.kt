package com.iwdael.permissionsdispatcher

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import java.lang.ref.WeakReference

class PermissionActivityLifecycle : Application.ActivityLifecycleCallbacks {
    val activities = mutableListOf<WeakReference<ActivityLauncher>>()
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        createActivityLauncher(activity)?.let {
            activities.add(0, WeakReference(it))
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
        activities.removeAll { it.get()?.activity == activity }
    }

    fun createActivityLauncher(activity: Activity): ActivityLauncher? {
        if (activity !is ComponentActivity) return null
        val activityLauncher = activities.firstOrNull { it.get()?.activity == activity }?.get()
        if (activityLauncher != null) return activityLauncher
        val callback = PermissionCallback()
        val launcher = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(), callback)
        return ActivityLauncher(activity, launcher, callback)
    }
}

data class ActivityLauncher(val activity: Activity, val launcher: ActivityResultLauncher<Array<String>>, val callback: PermissionCallback)