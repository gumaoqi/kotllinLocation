package com.gumaoqi.test.kotlinbaseproject.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.gumaoqi.test.kotlinbaseproject.R
import com.gumaoqi.test.kotlinbaseproject.R.id.*
import com.gumaoqi.test.kotlinbaseproject.adapter.LocationAdapter
import com.gumaoqi.test.kotlinbaseproject.base.BaseFragment
import com.gumaoqi.test.kotlinbaseproject.base.GuApplication
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.GET_USER_BY_ADMIN_BACK
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.GET_USER_LOCATION_BY_PHONE
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.SUCCESS
import com.gumaoqi.test.kotlinbaseproject.entity.GetBean
import com.gumaoqi.test.kotlinbaseproject.service.LoginService
import com.gumaoqi.test.kotlinbaseproject.tool.L
import com.gumaoqi.test.kotlinbaseproject.tool.Re
import com.gumaoqi.test.kotlinbaseproject.tool.S
import com.gumaoqi.test.kotlinbaseproject.tool.T
import kotlinx.android.synthetic.main.fragment_discount.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DisCountFragment : BaseFragment() {

    private lateinit var gHandler: Handler

    private lateinit var locationAdapter: LocationAdapter
    private lateinit var phoneAdapter: ArrayAdapter<String>
    private lateinit var phoneList: ArrayList<String>
    private var phone = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_discount, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        intData()
        setView()
    }

    override fun intData() {
        gHandler = Handler(Handler.Callback { msg ->
            if (activity == null) {//已经与activity解绑了
                return@Callback false
            }
            when (msg.arg1) {
                SUCCESS -> {
                }
                GET_USER_BY_ADMIN_BACK -> {
                    val getBean = msg.obj as GetBean
                    locationAdapter.adapterInfo = "请选择查询的手机号"
                    locationAdapter.notifyDataSetChanged()
                    phoneList = ArrayList()
                    phoneList.add("--请选择--")
                    if (getBean.results == null || getBean.results.isEmpty()) {
                    } else {
                        for (p in getBean.results) {
                            phoneList.add(p.c1 + "-" + p.c11)
                        }
                    }
                    phoneAdapter = ArrayAdapter(GuApplication.context, R.layout.item_drop_down, phoneList)
                    fragment_dis_sp.adapter = phoneAdapter
                }
                GET_USER_LOCATION_BY_PHONE -> {
                    val getBean = msg.obj as GetBean
                    if (getBean.results.isEmpty()) {
                        locationAdapter.adapterInfo = "暂无数据"
                        locationAdapter.setList(ArrayList())
                        return@Callback false
                    }
                    locationAdapter.adapterInfo = ""
                    locationAdapter.setList(getBean.results)
                    var phoneCount = 0
                    var locationEffectCount = 0
                    var number = 0
                    var maxInstance = 0.0
                    var maxTime = ""
                    val myPhoneList = ArrayList<String>()
                    for (p in getBean.results) {
                        number++
                        if (!p.c6.contains("E")) {
                            locationEffectCount++
                            if (p.c6.toDouble() > maxInstance) {
                                maxInstance = p.c6.toDouble()
                                maxTime = number.toString()
                            }
                        }
                        if (p.c5 !in myPhoneList) {
                            myPhoneList.add(p.c5)
                            phoneCount++
                        }

                    }
                    for (p in getBean.results) {
                    }
                    fragment_dis_tv.text = "定位总条数为：$number" +
                            "\n有效条数：$locationEffectCount" +
                            "\n上传位置的手机个数为：$phoneCount" +
                            "\n最大距离为：$maxInstance 米" +
                            "\n最大距离定位序号为：$maxTime "
                }
            }
            false
        })
        locationAdapter = LocationAdapter()
        locationAdapter.adapterInfo = "获取手机号中，请稍后"
        addParamGetUserByAdmin()
    }

    override fun setView() {
        super.setView()
        fragment_dis_rv.layoutManager = LinearLayoutManager(GuApplication.context)
        fragment_dis_rv.adapter = locationAdapter
        fragment_dis_sp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    return
                }
                fragment_dis_tv.text = ""
                locationAdapter.setList(ArrayList())
                locationAdapter.adapterInfo = "获取定位数据中，请稍后"
                addParamGetPhoneLocationList(phoneList[position].substring(0, 11))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    /**
     * onDestroyView中进行解绑操作
     */
    override fun onDestroyView() {
        super.onDestroyView()
    }

    /**
     * 添加参数去获取某个管理员的用户
     */
    private fun addParamGetUserByAdmin() {
        val paramMap = HashMap<String, String>()
        paramMap["c3"] = S.getString("c1")
        paramMap["tablename"] = "shop_user"
        getUserByAdminByRetrofit(paramMap, gHandler)
    }

    /**
     * 用retrofit获取某个管理员的用户
     */
    private fun getUserByAdminByRetrofit(paramMap: HashMap<String, String>, handler: Handler) {
        Re.serviceName = "f43174e44ad9f6f2/shopGetUser"
        Re.addSign(paramMap, GuApplication.context)
        val loginService = Re.getRetrofit()
                .create(LoginService::class.java)
        val call = loginService.login(paramMap)
        L.i(TAG, "获取某个管理员的用户")
        call.enqueue(object : Callback<GetBean> {
            override fun onResponse(call: Call<GetBean>, response: Response<GetBean>?) {
                if (response?.body() == null) {
                    L.i(TAG, "获取某个管理员的用户接口返回为空")
                    T.s(getString(R.string.gu_net_error_or_server_busy))
                    return
                }
                val getBean = response.body()
                L.i(TAG, "获取某个管理员的用户接口返回:$getBean")
                val message = handler.obtainMessage()
                message.arg1 = GET_USER_BY_ADMIN_BACK
                message.obj = getBean
                handler.sendMessageDelayed(message, 100)
            }

            override fun onFailure(call: Call<GetBean>, t: Throwable) {
                L.i(TAG, "获取某个管理员的用户接口连接超时,${t.message}")
                T.s(getString(R.string.gu_net_error_or_server_busy))
            }
        })
    }


    /**
     * 添加参数去获取某个手机号的位置列表
     */
    private fun addParamGetPhoneLocationList(phone: String) {
        val paramMap = HashMap<String, String>()
        paramMap["c5"] = phone
        paramMap["tablename"] = "map_location"
        getPhoneLocationListByRetrofit(paramMap, gHandler)
    }

    /**
     * 用retrofit去获取某个手机号的位置列表
     */
    private fun getPhoneLocationListByRetrofit(paramMap: HashMap<String, String>, handler: Handler) {
        Re.serviceName = "f43174e44ad9f6f2/shopGetUser"
        Re.addSign(paramMap, GuApplication.context)
        val loginService = Re.getRetrofit()
                .create(LoginService::class.java)
        val call = loginService.login(paramMap)
        L.i(TAG, "获取某个手机号的位置列表")
        call.enqueue(object : Callback<GetBean> {
            override fun onResponse(call: Call<GetBean>, response: Response<GetBean>?) {
                if (response?.body() == null) {
                    L.i(TAG, "获取某个手机号的位置列表接口返回为空")
                    T.s(getString(R.string.gu_net_error_or_server_busy))
                    return
                }
                val getBean = response.body()
                L.i(TAG, "获取某个手机号的位置列表接口返回:$getBean")
                val message = handler.obtainMessage()
                message.arg1 = GET_USER_LOCATION_BY_PHONE
                message.obj = getBean
                handler.sendMessageDelayed(message, 100)
            }

            override fun onFailure(call: Call<GetBean>, t: Throwable) {
                L.i(TAG, "获取某个手机号的位置列表接口连接超时,${t.message}")
                T.s(getString(R.string.gu_net_error_or_server_busy))
            }
        })
    }

}