package com.gumaoqi.test.kotlinbaseproject

import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.View
import com.gumaoqi.test.kotlinbaseproject.base.BaseActivity
import com.gumaoqi.test.kotlinbaseproject.base.GuModuleFragment
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.CHANGE_FIND_PASSWORD_FRAGMENT
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.CHANGE_LOGIN_FRAGMENT
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.CHANGE_REGISTER_FRAGMENT
import com.gumaoqi.test.kotlinbaseproject.fragment.FindPasswordFragment
import com.gumaoqi.test.kotlinbaseproject.fragment.LoginFragment
import com.gumaoqi.test.kotlinbaseproject.fragment.RegisterFragment
import com.gumaoqi.test.kotlinbaseproject.tool.L
import kotlinx.android.synthetic.main.title.*

class LoginActivity : BaseActivity() {

    private val loginFragment = LoginFragment()
    private val registerFragment = RegisterFragment()
    private val findPasswordFragment = FindPasswordFragment()
    private lateinit var gHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        iniData()
        setLayout()
    }

    override fun onResume() {
        super.onResume()
        setView()
    }

    private fun iniData() {
        gHandler = Handler(Handler.Callback { msg ->
            if (this == null) {//activity已经被销毁了
                return@Callback false
            }
            when (msg.arg1) {
                CHANGE_LOGIN_FRAGMENT -> {
                    L.i(TAG, "切换到登录页面")
                    title_title.setText(R.string.gu_login)
                    title_back.setText(R.string.gu_exit)
                    title_back.setOnClickListener { finish() }
                    changeFragment(R.id.activity_main_content_fl, loginFragment)
                }
                CHANGE_FIND_PASSWORD_FRAGMENT -> {
                    L.i(TAG, "切换到找回密码页面")
                    title_title.setText(R.string.gu_find_password)
                    title_back.setText(R.string.gu_back)
                    title_back.setOnClickListener {
                        val message = gHandler.obtainMessage()
                        message.arg1 = CHANGE_LOGIN_FRAGMENT
                        gHandler.sendMessageDelayed(message, 100)
                    }
                    changeFragment(R.id.activity_main_content_fl, findPasswordFragment)
                }
                CHANGE_REGISTER_FRAGMENT -> {
                    L.i(TAG, "切换到注册页面")
                    title_title.setText(R.string.gu_register)
                    title_back.setText(R.string.gu_back)
                    title_back.setOnClickListener {
                        val message = gHandler.obtainMessage()
                        message.arg1 = CHANGE_LOGIN_FRAGMENT
                        gHandler.sendMessageDelayed(message, 100)
                    }
                    changeFragment(R.id.activity_main_content_fl, registerFragment)
                }
            }
            false
        })
        doubleClickBack = true
    }

    private fun setLayout() {
        loginFragment.activityHandler = gHandler
        registerFragment.activityHandler = gHandler
        findPasswordFragment.activityHandler = gHandler
        val message = gHandler.obtainMessage()
        message.arg1 = CHANGE_LOGIN_FRAGMENT
        gHandler.sendMessageDelayed(message, 100)
    }


    private fun setView() {

    }
}