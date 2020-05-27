package com.gumaoqi.test.kotlinbaseproject.tool

import android.util.Log

/**
 * 单例类，log打印工具
 */
object L {
    fun i(TAG: String, content: String) {
        if (I.isShowLog) {
            Log.i(TAG, content)
        }
    }
}