package com.gumaoqi.test.kotlinbaseproject.base

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gumaoqi.test.kotlinbaseproject.R
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.SUCCESS

internal class GuModuleFragment : BaseFragment() {

    private lateinit var gHandler: Handler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.activity_gu_main, container, false)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        intData()
        setView()
    }


    override fun intData() {
        super.intData()
        gHandler = Handler(Handler.Callback { msg ->
            if (activity == null) {//已经与activity解绑了
                return@Callback false
            }
            when (msg.arg1) {
                SUCCESS -> {
                }
            }
            false
        })
    }

    override fun setView() {
        super.setView()
    }

    /**
     * onDestroyView中进行解绑操作
     */
    override fun onDestroyView() {
        super.onDestroyView()
    }

}