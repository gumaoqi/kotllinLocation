package com.gumaoqi.test.kotlinbaseproject.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gumaoqi.test.kotlinbaseproject.R
import com.gumaoqi.test.kotlinbaseproject.base.BaseFragment
import com.gumaoqi.test.kotlinbaseproject.base.GuApplication
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.CHANGE_LOGIN_FRAGMENT
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.GET_SMS_BACK
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.IF_REGISTER
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.REGISTER_BACK
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.SUCCESS
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.SURE_SMS_BACK
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.TIMER
import com.gumaoqi.test.kotlinbaseproject.entity.AddBean
import com.gumaoqi.test.kotlinbaseproject.entity.GetBean
import com.gumaoqi.test.kotlinbaseproject.entity.SmsBean
import com.gumaoqi.test.kotlinbaseproject.entity.SureSmsBean
import com.gumaoqi.test.kotlinbaseproject.service.AddService
import com.gumaoqi.test.kotlinbaseproject.service.GetSmsService
import com.gumaoqi.test.kotlinbaseproject.service.LoginService
import com.gumaoqi.test.kotlinbaseproject.service.SureSmsService
import com.gumaoqi.test.kotlinbaseproject.tool.L
import com.gumaoqi.test.kotlinbaseproject.tool.Re
import com.gumaoqi.test.kotlinbaseproject.tool.T
import kotlinx.android.synthetic.main.fragment_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : BaseFragment() {

    private lateinit var gHandler: Handler

    private var timer = 0
    var phone = "1"
    var sms = "1"
    var password = "1"
    var passwordAgain = "1"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        intData()
        setView()
    }

    @SuppressLint("SetTextI18n")
    override fun intData() {
        super.intData()
        gHandler = Handler(Handler.Callback { msg ->
            if (activity == null) {//已经与activity解绑了
                return@Callback false
            }
            when (msg.arg1) {
                SUCCESS -> {
                }
                IF_REGISTER -> {//判断是否已经注册了
                    val getBean = msg.obj as GetBean
                    if (getBean.results == null || getBean.results.isEmpty()) {//暂未注册去获取验证码
                        addParamGetSms(phone)
                    } else {//已经注册了，提示用户登录或者重置密码
                        T.s("该手机号已经注册，请登录或找回密码")
                    }
                }
                GET_SMS_BACK -> {
                    val smsBean = msg.obj as SmsBean
                    if (smsBean.smsId == null) {
                        T.s("获取验证码过于频繁，请稍后再试")
                    } else {//获取验证码成功，开启计时器
                        T.s("验证码获取成功，请查看短信")
                        timer = 120
                        val message = gHandler.obtainMessage()
                        message.arg1 = TIMER
                        gHandler.sendMessageDelayed(message, 1000)
                    }
                }

                TIMER -> {//开启计时器
                    timer--
                    if (timer > 0) {
                        fragment_register_sms_bt?.text = "" + timer + "s"
                        val message = gHandler.obtainMessage()
                        message.arg1 = TIMER
                        gHandler.sendMessageDelayed(message, 1000)
                    } else {
                        fragment_register_sms_bt?.setText(R.string.gu_get_sms)
                    }
                }
                SURE_SMS_BACK -> {
                    val sureSmsBean = msg.obj as SureSmsBean
                    if (sureSmsBean.msg == null) {//验证码填写错误
                        T.s("您填写的验证码有误")
                    } else {//验证码填写正确
                        addParamRegister()
                    }
                }
                REGISTER_BACK -> {
                    val addBean = msg.obj as AddBean
                    if (addBean.createdAt == null) {
                        T.s("注册失败，请重试")
                        return@Callback false
                    }
                    T.s("注册成功，请登录")
                    setMessageToActivity(CHANGE_LOGIN_FRAGMENT, 0)
                }
            }
            false
        })
        timer = 0
    }

    override fun setView() {
        super.setView()
        fragment_register_sms_bt.setOnClickListener {
            if (timer > 0) {
                T.s("120秒内只能够获取一次验证码")
                return@setOnClickListener
            }
            checkPhoneInput()
        }
        fragment_register_register_bt.setOnClickListener { checkPhoneSmsPasswordInput() }
    }

    /**
     * 检测手机号的输入
     */
    private fun checkPhoneInput() {
        phone = fragment_register_phone_et.text.toString()
        if (phone.length != 11 || (!phone.startsWith("1"))) {
            T.s("请输入11位有效手机号")
            return
        }
        addParamIfRegister(phone)
    }

    /**
     * 检测手机号验证码和密码的输入
     */
    private fun checkPhoneSmsPasswordInput() {
        phone = fragment_register_phone_et.text.toString()
        sms = fragment_register_sms_et.text.toString()
        password = fragment_register_password_et.text.toString()
        passwordAgain = fragment_register_password_again_et.text.toString()
        if (phone.length != 11 || (!phone.startsWith("1"))) {
            T.s("请输入11位有效手机号")
            return
        }
        if (sms.length != 6) {
            T.s("请输入6位有效手机验证码")
            return
        }
        if (password.length < 6 || passwordAgain.length < 6) {
            T.s("请输入6-18位有效密码")
            return
        }
        if (password != (passwordAgain)) {
            T.s("两次输入的密码不一致")
            return
        }
        addParamSureSms(phone, sms)
    }


    /**
     * 添加参数去判断是否已经注册
     */
    private fun addParamIfRegister(phone: String) {
        val paramMap = HashMap<String, String>()
        paramMap["c1"] = phone
        paramMap["tablename"] = "map_user"
        ifRegisterByRetrofit(paramMap, gHandler)
    }


    /**
     * 用retrofit判断是否已经注册
     */
    private fun ifRegisterByRetrofit(paramMap: HashMap<String, String>, handler: Handler) {
        Re.serviceName = "f43174e44ad9f6f2/shopGetUser"
        Re.addSign(paramMap, GuApplication.context)
        val loginService = Re.getRetrofit()
                .create(LoginService::class.java)
        val call = loginService.login(paramMap)
        L.i(TAG, "判断是否已经注册")
        call.enqueue(object : Callback<GetBean> {
            override fun onResponse(call: Call<GetBean>, response: Response<GetBean>?) {
                if (response?.body() == null) {
                    L.i(TAG, "判断是否已经注册接口返回为空")
                    T.s(getString(R.string.gu_net_error_or_server_busy))
                    return
                }
                val getBean = response.body()
                L.i(TAG, "判断是否已经注册接口返回:$getBean")
                val message = handler.obtainMessage()
                message.arg1 = IF_REGISTER
                message.obj = getBean
                handler.sendMessageDelayed(message, 100)
            }

            override fun onFailure(call: Call<GetBean>, t: Throwable) {
                L.i(TAG, "判断是否已经注册接口连接超时,${t.message}")
                T.s(getString(R.string.gu_net_error_or_server_busy))
            }
        })
    }

    /**
     * 添加参数去获取验证码
     */
    private fun addParamGetSms(phone: String) {
        val paramMap = HashMap<String, String>()
        paramMap["phone"] = phone
        getSmsByRetrofit(paramMap, gHandler)
    }

    /**
     * 用Retrofit去获取验证码
     */
    private fun getSmsByRetrofit(paramMap: HashMap<String, String>, handler: Handler) {
        Re.serviceName = "f43174e44ad9f6f2/shopSendSms"
        Re.addSign(paramMap, GuApplication.context)
        val getSmsService = Re.getRetrofit()
                .create(GetSmsService::class.java)
        val call = getSmsService.getSms(paramMap)
        L.i(TAG, "获取验证码")
        call.enqueue(object : Callback<SmsBean> {
            override fun onResponse(call: Call<SmsBean>, response: Response<SmsBean>?) {
                if (response?.body() == null) {
                    L.i(TAG, "获取验证码接口返回为空")
                    T.s(getString(R.string.gu_net_error_or_server_busy))
                    return
                }
                val smsBean = response.body()
                L.i(TAG, "获取验证码接口返回:$smsBean")
                val message = handler.obtainMessage()
                message.arg1 = GET_SMS_BACK
                message.obj = smsBean
                handler.sendMessageDelayed(message, 100)
            }

            override fun onFailure(call: Call<SmsBean>, t: Throwable) {
                L.i(TAG, "获取验证码接口连接超时,${t.message}")
                T.s(getString(R.string.gu_net_error_or_server_busy))
            }
        })
    }


    /**
     * 添加参数去验证验证码
     */
    private fun addParamSureSms(phone: String, sms: String) {
        val paramMap = HashMap<String, String>()
        paramMap["phone"] = phone
        paramMap["sms"] = sms
        sureSmsByRetrofit(paramMap, gHandler)
    }

    /**
     * 用Retrofit去验证验证码
     */
    private fun sureSmsByRetrofit(paramMap: HashMap<String, String>, handler: Handler) {
        Re.serviceName = "f43174e44ad9f6f2/shopSureSms"
        Re.addSign(paramMap, GuApplication.context)
        val sureSmsService = Re.getRetrofit()
                .create(SureSmsService::class.java)
        val call = sureSmsService.sureSms(paramMap)
        L.i(TAG, "验证验证码")
        call.enqueue(object : Callback<SureSmsBean> {
            override fun onResponse(call: Call<SureSmsBean>, response: Response<SureSmsBean>?) {
                if (response?.body() == null) {
                    L.i(TAG, "验证验证码接口返回为空")
                    T.s(getString(R.string.gu_net_error_or_server_busy))
                    return
                }
                val smsBean = response.body()
                L.i(TAG, "验证验证码接口返回:$smsBean")
                val message = handler.obtainMessage()
                message.arg1 = SURE_SMS_BACK
                message.obj = smsBean
                handler.sendMessageDelayed(message, 100)
            }

            override fun onFailure(call: Call<SureSmsBean>, t: Throwable) {
                L.i(TAG, "验证验证码接口连接超时,${t.message}")
                T.s(getString(R.string.gu_net_error_or_server_busy))
            }
        })
    }

    /**
     * 添加参数进行注册
     */
    private fun addParamRegister() {
        val paramMap = HashMap<String, String>()
        paramMap["c1"] = phone
        paramMap["c2"] = password
        paramMap["tablename"] = "map_user"
        registerByRetrofit(paramMap, gHandler)
    }


    /**
     * 用retrofit进行注册
     */
    private fun registerByRetrofit(paramMap: HashMap<String, String>, handler: Handler) {
        Re.serviceName = "f43174e44ad9f6f2/shopAddUser"
        Re.addSign(paramMap, GuApplication.context)
        val addService = Re.getRetrofit()
                .create(AddService::class.java)
        val call = addService.add(paramMap)
        L.i(TAG, "进行注册")
        call.enqueue(object : Callback<AddBean> {
            override fun onResponse(call: Call<AddBean>, response: Response<AddBean>?) {
                if (response?.body() == null) {
                    L.i(TAG, "进行注册接口返回为空")
                    T.s(getString(R.string.gu_net_error_or_server_busy))
                    return
                }
                val addBean = response.body()
                L.i(TAG, "进行注册接口返回:$addBean")
                val message = handler.obtainMessage()
                message.arg1 = REGISTER_BACK
                message.obj = addBean
                handler.sendMessageDelayed(message, 100)
            }

            override fun onFailure(call: Call<AddBean>, t: Throwable) {
                L.i(TAG, "进行注册接口连接超时,${t.message}")
                T.s(getString(R.string.gu_net_error_or_server_busy))
            }
        })
    }

}