package com.gumaoqi.test.kotlinbaseproject.base

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gumaoqi.test.kotlinbaseproject.R
import com.gumaoqi.test.kotlinbaseproject.tool.L

open class BaseFragment : Fragment() {
    val TAG = javaClass.simpleName

    lateinit var activityHandler: Handler

    override fun onAttach(context: Context) {
        super.onAttach(context)
        L.i(TAG, "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        L.i(TAG, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        L.i(TAG, "onCreateView")
        return inflater.inflate(R.layout.activity_gu_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        L.i(TAG, "onActivityCreated")
    }

    override fun onStart() {
        super.onStart()
        L.i(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
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

    override fun onDestroyView() {
        super.onDestroyView()
        L.i(TAG, "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        L.i(TAG, "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        L.i(TAG, "onDetach")
    }


    open fun intData() {
        L.i(TAG, "intData")
    }

    open fun setView() {
        L.i(TAG, "setView")
    }


    /**
     * 向activity发送消息
     */
    fun setMessageToActivity(arg1: Int, arg2: Int) {
        if (::activityHandler.isInitialized) {
            activityHandler.let {
                var message = it.obtainMessage()
                message.arg1 = arg1
                message.arg2 = arg2
                it.sendMessageDelayed(message, 100)
            }
        }
    }
}