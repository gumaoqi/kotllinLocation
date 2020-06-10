package com.gumaoqi.test.kotlinbaseproject.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.gumaoqi.test.kotlinbaseproject.R
import com.gumaoqi.test.kotlinbaseproject.adapter.LocationAdapter
import com.gumaoqi.test.kotlinbaseproject.base.BaseFragment
import com.gumaoqi.test.kotlinbaseproject.base.GuApplication
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.GET_MY_LOCATION_LIST_BACK
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.SUCCESS
import com.gumaoqi.test.kotlinbaseproject.entity.GetBean
import com.gumaoqi.test.kotlinbaseproject.service.LoginService
import com.gumaoqi.test.kotlinbaseproject.tool.L
import com.gumaoqi.test.kotlinbaseproject.tool.Re
import com.gumaoqi.test.kotlinbaseproject.tool.S
import com.gumaoqi.test.kotlinbaseproject.tool.T
import kotlinx.android.synthetic.main.fragment_action.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActionFragment : BaseFragment() {

    private lateinit var gHandler: Handler
    private lateinit var locationAdapter: LocationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_action, container, false)
    }

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
                GET_MY_LOCATION_LIST_BACK -> {
                    val getBean = msg.obj as GetBean
                    if (getBean.results.isEmpty()) {
                        return@Callback false
                    }
                    locationAdapter.adapterInfo = ""
                    locationAdapter.setList(getBean.results)
                }
            }
            false
        })
        locationAdapter = LocationAdapter()
        locationAdapter.adapterInfo = "获取位置中"
        addParamGetMyLocationList()
    }

    override fun setView() {
        super.setView()
        fragment_action_rv.layoutManager = LinearLayoutManager(GuApplication.context)
        fragment_action_rv.adapter = locationAdapter
        fragment_action_srl.setOnRefreshListener {
            intData()
            setView()
            fragment_action_srl.isRefreshing = false
        }
    }

    /**
     * onDestroyView中进行解绑操作
     */
    override fun onDestroyView() {
        super.onDestroyView()
    }

    /**
     * 添加参数去获取我的位置列表
     */
    private fun addParamGetMyLocationList() {
        val paramMap = HashMap<String, String>()
        paramMap["c5"] = S.getString("c1")
        paramMap["tablename"] = "map_location"
        getMyLocationListByRetrofit(paramMap, gHandler)
    }

    /**
     * 用retrofit去获取我的位置列表
     */
    private fun getMyLocationListByRetrofit(paramMap: HashMap<String, String>, handler: Handler) {
        Re.serviceName = "f43174e44ad9f6f2/shopGetUser"
        Re.addSign(paramMap, GuApplication.context)
        val loginService = Re.getRetrofit()
                .create(LoginService::class.java)
        val call = loginService.login(paramMap)
        L.i(TAG, "获取我的位置列表")
        call.enqueue(object : Callback<GetBean> {
            override fun onResponse(call: Call<GetBean>, response: Response<GetBean>?) {
                if (response?.body() == null) {
                    L.i(TAG, "获取我的位置列表接口返回为空")
                    T.s(getString(R.string.gu_net_error_or_server_busy))
                    return
                }
                val getBean = response.body()
                L.i(TAG, "获取我的位置列表接口返回:$getBean")
                val message = handler.obtainMessage()
                message.arg1 = GET_MY_LOCATION_LIST_BACK
                message.obj = getBean
                handler.sendMessageDelayed(message, 100)
            }

            override fun onFailure(call: Call<GetBean>, t: Throwable) {
                L.i(TAG, "获取我的位置列表接口连接超时,${t.message}")
                T.s(getString(R.string.gu_net_error_or_server_busy))
            }
        })
    }
}