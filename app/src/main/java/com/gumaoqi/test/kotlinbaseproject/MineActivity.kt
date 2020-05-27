package com.gumaoqi.test.kotlinbaseproject

import android.os.Bundle
import android.os.Handler
import com.gumaoqi.test.kotlinbaseproject.base.BaseActivity
import com.gumaoqi.test.kotlinbaseproject.base.GuModuleFragment
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.MY_INFO_FRAGMENT
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.UPDATE_HEAD_IMG_FRAGMENT
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.UPDATE_PASSWORD_FRAGMENT
import com.gumaoqi.test.kotlinbaseproject.fragment.MineFragment
import com.gumaoqi.test.kotlinbaseproject.fragment.UpdateHeadImgFragment
import com.gumaoqi.test.kotlinbaseproject.fragment.UpdateInfoFragment
import com.gumaoqi.test.kotlinbaseproject.fragment.UpdatePasswordFragment
import kotlinx.android.synthetic.main.title.*

class MineActivity : BaseActivity() {

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
                MY_INFO_FRAGMENT -> {
                    title_title.text = "我的信息"
                    title_back.text = "返回"
                    title_back.setOnClickListener { finish() }
                    changeFragment(R.id.activity_main_content_fl, UpdateInfoFragment())
                }
                UPDATE_PASSWORD_FRAGMENT -> {
                    title_title.text = "修改密码"
                    title_back.text = "返回"
                    title_back.setOnClickListener { finish() }
                    changeFragment(R.id.activity_main_content_fl, UpdatePasswordFragment())
                }
                UPDATE_HEAD_IMG_FRAGMENT -> {
                    title_title.text = "修改头像"
                    title_back.text = "返回"
                    title_back.setOnClickListener { finish() }
                    changeFragment(R.id.activity_main_content_fl, UpdateHeadImgFragment())
                }
            }
            false
        })
    }

    private fun setLayout() {
        when (intent.getStringExtra("fragment")) {
            "1" -> {
                val message = gHandler.obtainMessage()
                message.arg1 = MY_INFO_FRAGMENT
                gHandler.sendMessageDelayed(message, 100)
            }
            "2" -> {
                val message = gHandler.obtainMessage()
                message.arg1 = UPDATE_PASSWORD_FRAGMENT
                gHandler.sendMessageDelayed(message, 100)
            }
            "3" -> {
                val message = gHandler.obtainMessage()
                message.arg1 = UPDATE_HEAD_IMG_FRAGMENT
                gHandler.sendMessageDelayed(message, 100)
            }
        }
    }


    private fun setView() {

    }
}