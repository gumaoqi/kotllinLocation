package com.gumaoqi.test.kotlinbaseproject.base

import android.app.Activity

/**
 *Activity的集合，单例类
 */
object ActivityCollector {
    private val activities = ArrayList<Activity>()

    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    fun removeActivity(activity: Activity) {
        activities.remove(activity)
    }

    fun finishAll() {
        for (activity in activities) {
            if (!activity.isFinishing) {
                activity.finish()
            }
        }
        activities.clear()
    }
}