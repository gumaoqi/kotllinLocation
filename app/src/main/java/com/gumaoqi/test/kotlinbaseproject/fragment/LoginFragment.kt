package com.gumaoqi.test.kotlinbaseproject.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gumaoqi.test.kotlinbaseproject.HomeActivity
import com.gumaoqi.test.kotlinbaseproject.R
import com.gumaoqi.test.kotlinbaseproject.base.BaseFragment
import com.gumaoqi.test.kotlinbaseproject.base.GuApplication
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.CHANGE_FIND_PASSWORD_FRAGMENT
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.CHANGE_REGISTER_FRAGMENT
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.LOGIN_BACK
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.SUCCESS
import com.gumaoqi.test.kotlinbaseproject.entity.GetBean
import com.gumaoqi.test.kotlinbaseproject.service.LoginService
import com.gumaoqi.test.kotlinbaseproject.tool.L
import com.gumaoqi.test.kotlinbaseproject.tool.Re
import com.gumaoqi.test.kotlinbaseproject.tool.S
import com.gumaoqi.test.kotlinbaseproject.tool.T
import kotlinx.android.synthetic.main.fragment_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : BaseFragment() {

    private lateinit var gHandler: Handler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        intData()
        setView()
    }

    override fun intData() {
        super.intData()
        gHandler = Handler(Handler.Callback { msg ->
            if (fragment_login_login_bt == null) {//已经与activity解绑了
                return@Callback false
            }
            when (msg.arg1) {
                SUCCESS -> {
                }
                LOGIN_BACK -> {
                    val getBean = msg.obj as GetBean
                    if (getBean.results == null || getBean.results.isEmpty()) {
                        T.s("登录失败，账号或密码错误")
                    } else {
                        val result = getBean.results[0]
                        T.s("登录成功")
                        S.setString("login_time", "" + System.currentTimeMillis())
                        S.setString("c1", "" + result.c1)
                        S.setString("c2", "" + result.c2)
                        S.setString("c3", "" + result.c3)
                        S.setString("c4", "" + result.c4)
                        S.setString("c5", "" + result.c5)
                        S.setString("c6", "" + result.c6)
                        S.setString("c7", "" + result.c7)
                        S.setString("c8", "" + result.c8)
                        S.setString("c9", "" + result.c9)
                        S.setString("c10", "" + result.c10)
                        S.setString("c11", "" + result.c11)
                        S.setString("object_id", "" + result.objectId)
                        S.setString("create_time", "" + result.createdAt)
                        val intent = Intent(GuApplication.context, HomeActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }
                }
            }
            false
        })
    }

    override fun setView() {
        super.setView()
        fragment_login_login_bt.setOnClickListener { checkInput() }
        fragment_login_find_password_tv.setOnClickListener { setMessageToActivity(CHANGE_FIND_PASSWORD_FRAGMENT, 0) }
        fragment_login_register_bt.setOnClickListener { setMessageToActivity(CHANGE_REGISTER_FRAGMENT, 0) }
    }

    /**
     * onDestroyView中进行解绑操作
     */
    override fun onDestroyView() {
        super.onDestroyView()
    }

    /**
     * 检查输入内容的合法性
     */
    private fun checkInput() {
        val phone = fragment_login_phone_et.text.toString()
        val password = fragment_login_password_et.text.toString()
        if (phone.length != 11) {
            T.s(R.string.gu_input_phone)
            return
        }
        if (password.length < 6) {
            T.s(R.string.gu_input_password)
            return
        }
        addParamLogin(phone, password)
    }

    /**
     * 添加参数去登录
     */
    private fun addParamLogin(phone: String, password: String) {
        val paramMap = HashMap<String, String>()
        paramMap["c1"] = phone
        paramMap["c2"] = password
        paramMap["tablename"] = "shop_user"
        loginByRetrofit(paramMap, gHandler)
    }

    /**
     * 用retrofit登录
     */
    private fun loginByRetrofit(paramMap: HashMap<String, String>, handler: Handler) {
        Re.serviceName = "f43174e44ad9f6f2/shopGetUser"
        Re.addSign(paramMap, GuApplication.context)
        val loginService = Re.getRetrofit()
                .create(LoginService::class.java)
        val call = loginService.login(paramMap)
        L.i(TAG, "登录")
        call.enqueue(object : Callback<GetBean> {
            override fun onResponse(call: Call<GetBean>, response: Response<GetBean>?) {
                if (response?.body() == null) {
                    L.i(TAG, "登录接口返回为空")
                    T.s(getString(R.string.gu_net_error_or_server_busy))
                    return
                }
                val getBean = response.body()
                L.i(TAG, "登录接口返回:$getBean")
                val message = handler.obtainMessage()
                message.arg1 = LOGIN_BACK
                message.obj = getBean
                handler.sendMessageDelayed(message, 100)
            }

            override fun onFailure(call: Call<GetBean>, t: Throwable) {
                L.i(TAG, "登录接口连接超时,${t.message}")
                T.s(getString(R.string.gu_net_error_or_server_busy))
            }
        })
    }
}