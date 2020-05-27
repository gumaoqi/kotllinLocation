package com.gumaoqi.test.kotlinbaseproject.fragment

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gumaoqi.test.kotlinbaseproject.R
import com.gumaoqi.test.kotlinbaseproject.base.BaseFragment
import com.gumaoqi.test.kotlinbaseproject.base.GuApplication
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.IF_ADMIN_BACK
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.SUCCESS
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.UPDATE_INFO
import com.gumaoqi.test.kotlinbaseproject.entity.GetBean
import com.gumaoqi.test.kotlinbaseproject.entity.UpdateBean
import com.gumaoqi.test.kotlinbaseproject.service.LoginService
import com.gumaoqi.test.kotlinbaseproject.service.UpdateService
import com.gumaoqi.test.kotlinbaseproject.tool.L
import com.gumaoqi.test.kotlinbaseproject.tool.Re
import com.gumaoqi.test.kotlinbaseproject.tool.S
import com.gumaoqi.test.kotlinbaseproject.tool.T
import kotlinx.android.synthetic.main.fragment_update_info.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateInfoFragment : BaseFragment() {

    private lateinit var gHandler: Handler

    private var successKey = ""
    private var successValue = ""
    private var value = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_update_info, container, false)
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
                UPDATE_INFO -> {
                    val updateBean = msg.obj as UpdateBean
                    if (updateBean.updatedAt == null) {
                        T.s("更新个人资料失败，请重试")
                        return@Callback false
                    }
                    T.s("更新个人资料成功")
                    L.i(TAG, "更新个人资料成功$successKey=$successValue")
                    S.setString(successKey, successValue)
                    intData()
                    setView()
                }
                IF_ADMIN_BACK -> {
                    val getBean = msg.obj as GetBean
                    if (getBean.results == null || getBean.results.isEmpty()) {
                        T.s("输入的手机号不是管理员")
                    } else {
                        val result = getBean.results[0]
                        S.setString("c3", value)
                        S.setString("c7", result.c4)
                        S.setString("c8", result.c5)
                        S.setString("c9", result.c6)
                        addParamUpdateInfo(value, result.c4, result.c5, result.c6)
                    }
                }
            }
            false
        })
    }

    override fun setView() {
        super.setView()
        fragment_update_info_c3_et.setText(S.getString("c3"))
        fragment_update_info_c4_et.setText(S.getString("c4"))
        fragment_update_info_c5_et.setText(S.getString("c5"))
        fragment_update_info_c6_et.setText(S.getString("c6"))
        fragment_update_info_c7_et.setText(S.getString("c7"))
        fragment_update_info_c8_et.setText(S.getString("c8"))
        fragment_update_info_c9_et.setText(S.getString("c9"))
        fragment_update_info_c10_et.setText(S.getString("c11"))
        fragment_update_info_c3_bt.setOnClickListener {
            value = fragment_update_info_c3_et.text.toString()
            checkInput("c3", value)
        }
        fragment_update_info_c4_bt.setOnClickListener {
            val value = fragment_update_info_c4_et.text.toString()
            checkInput("c4", value)
        }
        fragment_update_info_c5_bt.setOnClickListener {
            val value = fragment_update_info_c5_et.text.toString()
            checkInput("c5", value)
        }
        fragment_update_info_c6_bt.setOnClickListener {
            val value = fragment_update_info_c6_et.text.toString()
            checkInput("c6", value)
        }
        fragment_update_info_c7_bt.setOnClickListener {
            val value = fragment_update_info_c7_et.text.toString()
            checkInput("c7", value)
        }
        fragment_update_info_c8_bt.setOnClickListener {
            val value = fragment_update_info_c8_et.text.toString()
            checkInput("c8", value)
        }
        fragment_update_info_c9_bt.setOnClickListener {
            val value = fragment_update_info_c9_et.text.toString()
            checkInput("c9", value)
        }
        fragment_update_info_c10_bt.setOnClickListener {
            val value = fragment_update_info_c10_et.text.toString()
            checkInputNickName("c11", value)
        }
        if (S.getString("c10") != "1") {
            fragment_update_info_one_ll.visibility = View.GONE
            fragment_update_info_two_ll.visibility = View.GONE
            fragment_update_info_three_ll.visibility = View.GONE
        }
    }

    /**
     * onDestroyView中进行解绑操作
     */
    override fun onDestroyView() {
        super.onDestroyView()
    }


    private fun checkInput(key: String, value: String) {
        if (value.isEmpty() || value.length != 11) {
            T.s("管理员必须是11位的手机号码")
            return
        }
        addParamIfadmin(value)
//        addParamUpdateInfo(key, value)
    }

    /**
     * 添加参数修改个人信息
     */
    private fun addParamUpdateInfo(phone: String, weidu: String, jingdu: String, addr: String) {
        val paramMap = HashMap<String, String>()
        paramMap["c3"] = phone
        paramMap["c7"] = weidu
        paramMap["c8"] = jingdu
        paramMap["c9"] = addr
        paramMap["objectid"] = S.getString("object_id")
        paramMap["tablename"] = "shop_user"
        updateInfoByRetrofit(paramMap, gHandler)
    }

    private fun checkInputNickName(key: String, value: String) {
        if (value.isEmpty()) {
            T.s("请输入你的新昵称")
            return
        }
//        addParamIfadmin(value)
        addParamUpdateNickName(key, value)
    }

    /**
     * 添加参数修改个人信息
     */
    private fun addParamUpdateNickName(key: String, value: String) {
        val paramMap = HashMap<String, String>()
        paramMap[key] = value
        paramMap["objectid"] = S.getString("object_id")
        paramMap["tablename"] = "shop_user"
        updateInfoByRetrofit(paramMap, gHandler)
    }


    /**
     * 用retrofit修改个人信息
     */
    private fun updateInfoByRetrofit(paramMap: HashMap<String, String>, handler: Handler) {
        Re.serviceName = "f43174e44ad9f6f2/shopUpdateUser"
        Re.addSign(paramMap, GuApplication.context)
        val updateService = Re.getRetrofit()
                .create(UpdateService::class.java)
        val call = updateService.update(paramMap)
        L.i(TAG, "修改个人信息")
        call.enqueue(object : Callback<UpdateBean> {
            override fun onResponse(call: Call<UpdateBean>, response: Response<UpdateBean>?) {
                if (response?.body() == null) {
                    L.i(TAG, "修改个人信息接口返回为空")
                    T.s(getString(R.string.gu_net_error_or_server_busy))
                    return
                }
                for (key in paramMap.keys) {
                    if (key.startsWith("c")) {
                        successKey = key
                        successValue = paramMap[key]!!
                    }
                }
                val updateBean = response.body()
                L.i(TAG, "修改个人信息接口返回:$updateBean")
                val message = handler.obtainMessage()
                message.arg1 = UPDATE_INFO
                message.obj = updateBean
                handler.sendMessageDelayed(message, 100)
            }

            override fun onFailure(call: Call<UpdateBean>, t: Throwable) {
                L.i(TAG, "修改个人信息接口连接超时,${t.message}")
                T.s(getString(R.string.gu_net_error_or_server_busy))
            }
        })
    }

    /**
     * 添加参数去检查输入是否为管理员
     */
    private fun addParamIfadmin(phone: String) {
        val paramMap = HashMap<String, String>()
        paramMap["c1"] = phone
        paramMap["c10"] = "1"
        paramMap["tablename"] = "shop_user"
        loginByRetrofit(paramMap, gHandler)
    }

    /**
     * 用retrofit检查输入是否为管理员
     */
    private fun loginByRetrofit(paramMap: HashMap<String, String>, handler: Handler) {
        Re.serviceName = "f43174e44ad9f6f2/shopGetUser"
        Re.addSign(paramMap, GuApplication.context)
        val loginService = Re.getRetrofit()
                .create(LoginService::class.java)
        val call = loginService.login(paramMap)
        L.i(TAG, "检查输入是否为管理员")
        call.enqueue(object : Callback<GetBean> {
            override fun onResponse(call: Call<GetBean>, response: Response<GetBean>?) {
                if (response?.body() == null) {
                    L.i(TAG, "检查输入是否为管理员接口返回为空")
                    T.s(getString(R.string.gu_net_error_or_server_busy))
                    return
                }
                val getBean = response.body()
                L.i(TAG, "检查输入是否为管理员接口返回:$getBean")
                val message = handler.obtainMessage()
                message.arg1 = HandlerArg.IF_ADMIN_BACK
                message.obj = getBean
                handler.sendMessageDelayed(message, 100)
            }

            override fun onFailure(call: Call<GetBean>, t: Throwable) {
                L.i(TAG, "检查输入是否为管理员接口连接超时,${t.message}")
                T.s(getString(R.string.gu_net_error_or_server_busy))
            }
        })
    }
}