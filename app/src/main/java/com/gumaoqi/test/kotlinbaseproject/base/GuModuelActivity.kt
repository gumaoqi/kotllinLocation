package com.gumaoqi.test.kotlinbaseproject.base

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.gumaoqi.test.kotlinbaseproject.LoginActivity
import com.gumaoqi.test.kotlinbaseproject.R
import com.gumaoqi.test.kotlinbaseproject.tool.L
import kotlinx.android.synthetic.main.activity_gu_main.*
import kotlinx.android.synthetic.main.title.*

internal class GuModuleActivity : BaseActivity() {

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

            }
            false
        })
    }

    private fun setLayout() {
        title_title.setText(R.string.gu_model)
//        title_title.visibility = View.GONE
        changeFragment(R.id.activity_main_content_fl, GuModuleFragment())
    }

    private fun setView() {

    }
}