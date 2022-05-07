package com.cmk.app.util.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.cmk.app.ext.loge

/**
 * @Author: romens
 * @Date: 2019-12-16 14:04
 * @Desc:
 */
class KtxLifeCycleCallBack : Application.ActivityLifecycleCallbacks{
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        ActivityStackManager.pushActivity(activity)
        "onActivityCreated : ${activity.localClassName}".loge()
    }

    override fun onActivityStarted(activity: Activity) {
        "onActivityStarted : ${activity.localClassName}".loge()
    }

    override fun onActivityResumed(activity: Activity) {
        "onActivityResumed : ${activity.localClassName}".loge()
    }

    override fun onActivityPaused(activity: Activity) {
        "onActivityPaused : ${activity.localClassName}".loge()
    }


    override fun onActivityDestroyed(activity: Activity) {
        "onActivityDestroyed : ${activity.localClassName}".loge()
        ActivityStackManager.popActivity(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
        "onActivityStopped : ${activity.localClassName}".loge()
    }
}