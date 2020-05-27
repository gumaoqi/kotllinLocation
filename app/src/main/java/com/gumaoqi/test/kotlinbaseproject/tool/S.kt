package com.gumaoqi.test.kotlinbaseproject.tool

import android.content.Context
import com.gumaoqi.test.kotlinbaseproject.base.GuApplication.Companion.context
import com.gumaoqi.test.kotlinbaseproject.base.GuApplication.Companion.loginEffectTime

/**
 * 单例类，sharedPreferences使用工具
 */
object S {
    /**
     * 根据key获取数据，如果没有返回""
     *
     * @param string key
     * @return
     */
    fun getString(string: String): String =
            context.getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE).getString(string, "")


    /**
     *
     * @param key
     * @param value
     */

    /**
     * 保存数据
     *
     * @param key   key
     * @param value value
     */
    fun setString(key: String, value: String) {
        val sp = context.getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(key, value)
        editor.commit()
    }

    /**
     * 清除所有的数据
     */
    fun clearSharedPreferences() {
        val sp = context.getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.clear()
        editor.commit()
        return
    }

    /**
     * 是否需要登录
     *
     * @return
     */
    fun ifNeedLogin(): Boolean {
        if (getString("login_time").isEmpty()) {//没有登录
            return true
        }
        if (System.currentTimeMillis() - getString("login_time").toLong() > loginEffectTime) {//已登录，但已经过期
            T.s("您的登录已过期")
            clearSharedPreferences()
            return true
        }
        return false
    }
}