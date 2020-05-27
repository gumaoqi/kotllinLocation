package com.gumaoqi.test.kotlinbaseproject.tool

import android.widget.Toast
import com.gumaoqi.test.kotlinbaseproject.base.GuApplication.Companion.context

/**
 * 单例类，toast打印工具
 */
object T {
    private lateinit var toast: Toast//延迟初始化toast
    fun s(content: String) {
        val myContext = context.applicationContext
        if (T::toast.isInitialized) {//如果当前已经存在toast，则将其取消
            toast.cancel()
        }
        toast = Toast.makeText(myContext, content, Toast.LENGTH_SHORT)
        toast.show()
    }

    fun s(int: Int) {
        val str = context.getString(int)
        s(str)
    }
}