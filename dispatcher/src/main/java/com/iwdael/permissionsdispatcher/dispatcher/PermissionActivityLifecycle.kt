package com.iwdael.permissionsdispatcher.dispatcher

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import java.lang.ref.WeakReference

class PermissionActivityLifecycle : Application.ActivityLifecycleCallbacks {
    val activities = mutableListOf<WeakReference<Activity>>()
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activities.add(0, WeakReference(activity))
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
        activities.forEach { weak ->
            if (weak.get() == activity) {
                activities.remove(weak)
                return@forEach
            }
        }
    }
}