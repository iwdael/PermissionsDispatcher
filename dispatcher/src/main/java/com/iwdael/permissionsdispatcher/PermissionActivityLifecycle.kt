package com.iwdael.permissionsdispatcher

import android.app.Activity
import android.app.Application
import android.os.Bundle
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
        activities.removeAll { it.get() == activity }
    }
}