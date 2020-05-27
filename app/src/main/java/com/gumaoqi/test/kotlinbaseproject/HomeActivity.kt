package com.gumaoqi.test.kotlinbaseproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.gumaoqi.test.kotlinbaseproject.base.BaseActivity
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.CHANGE_FIVE
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.CHANGE_FOUR
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.CHANGE_OME
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.CHANGE_THREE
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.CHANGE_TWO
import com.gumaoqi.test.kotlinbaseproject.fragment.GuBottomNavigationFragment
import com.gumaoqi.test.kotlinbaseproject.fragment.*
import com.gumaoqi.test.kotlinbaseproject.location.MyService
import com.gumaoqi.test.kotlinbaseproject.tool.L
import kotlinx.android.synthetic.main.title.*

class HomeActivity : BaseActivity() {

    private lateinit var gHandler: Handler
    private val bottomNavigationFragment = GuBottomNavigationFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
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
                CHANGE_OME -> {
                    L.i(TAG, "页面一")
                    title_back.text = ""
                    title_title.text = "定位"
                    changeFragment(R.id.activity_home_content_fl, HomeFragment())
                }
                CHANGE_TWO -> {
                    title_back.text = ""
                    title_title.text = "列表"
                    changeFragment(R.id.activity_home_content_fl, ActionFragment())
                }
                CHANGE_THREE -> {
                    title_back.text = ""
                    title_title.text = "查询"
                    changeFragment(R.id.activity_home_content_fl, DisCountFragment())
                }
                CHANGE_FOUR -> {
                    title_back.text = ""
                    title_title.text = "其他"
                    changeFragment(R.id.activity_home_content_fl, RecommendFragment())
                }
                CHANGE_FIVE -> {
                    title_back.text = ""
                    title_title.text = "我的"
                    changeFragment(R.id.activity_home_content_fl, MineFragment())

                }
            }
            false
        })
        doubleClickBack = true
    }

    private fun setLayout() {
        bottomNavigationFragment.activityHandler = gHandler
        changeFragment(R.id.activity_home_bottom_navigation_fl, bottomNavigationFragment)
        val intent = Intent(this, MyService::class.java)
        startService(intent)
//        val message = gHandler.obtainMessage()
//        message.arg1 = CHANGE_OME
//        gHandler.sendMessageDelayed(message, 100)
    }


    private fun setView() {

    }
}