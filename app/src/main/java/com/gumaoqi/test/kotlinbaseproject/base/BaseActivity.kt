package com.gumaoqi.test.kotlinbaseproject.base

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.gumaoqi.test.kotlinbaseproject.R
import com.gumaoqi.test.kotlinbaseproject.tool.L
import com.gumaoqi.test.kotlinbaseproject.tool.T

/**
 * 所有activity的基础类
 */
open class BaseActivity : AppCompatActivity() {
    val TAG = javaClass.simpleName

    var doubleClickBack = false
    var lastClickBackTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        L.i(TAG, "onCreate")
        ActivityCollector.addActivity(this)
        //让系统永不锁屏并关闭了屏保界面
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //让软键盘总是隐藏
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        //隐藏状态栏
        supportActionBar?.hide()
        setContentView(R.layout.activity_gu_main)
    }

    override fun onStart() {
        super.onStart()
        L.i(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        hideSoftKeyBoard()
        L.i(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        L.i(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        L.i(TAG, "onStop")
    }

    override fun onRestart() {
        super.onRestart()
        L.i(TAG, "onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        L.i(TAG, "onDestroy")
        ActivityCollector.removeActivity(this)
    }

    /**
     * 切换fragment
     * @param flameId flameLayout的id
     * @param fragment 需要切换的fragment
     */
    fun changeFragment(flameId: Int, fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction().replace(flameId, fragment).commit()
    }

    /**
     * @param flameId flameLayout的id
     *
     * @return 返回当前的fragment
     */
    fun getFragment(flameId: Int): androidx.fragment.app.Fragment? {
        return supportFragmentManager.findFragmentById(flameId)
    }

    /**
     * 隐藏软键盘
     */
    fun hideSoftKeyBoard() {
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun onBackPressed() {
        if (doubleClickBack) {
            val clickBackTime = System.currentTimeMillis()
            if (clickBackTime - lastClickBackTime > 3000) {
                T.s("再次点击退出系统")
                lastClickBackTime = clickBackTime
                return
            }
        }
        super.onBackPressed()
    }
}