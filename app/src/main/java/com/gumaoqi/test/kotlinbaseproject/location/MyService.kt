package com.gumaoqi.test.kotlinbaseproject.location

import android.app.*
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import com.baidu.location.BDLocation
import com.gumaoqi.test.kotlinbaseproject.R
import com.gumaoqi.test.kotlinbaseproject.base.GuApplication
import com.gumaoqi.test.kotlinbaseproject.base.GuApplication.Companion.lastUploadLocationTime
import com.gumaoqi.test.kotlinbaseproject.base.GuApplication.Companion.notification
import com.gumaoqi.test.kotlinbaseproject.base.GuApplication.Companion.setNotification
import com.gumaoqi.test.kotlinbaseproject.base.GuApplication.Companion.uploadLocationTime
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.SERVICE_GET_LOCATION
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.SERVICE_GET_LOCATION_BACK
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.SERVICE_UPLOAD_LOCATION_BACK
import com.gumaoqi.test.kotlinbaseproject.entity.AddBean
import com.gumaoqi.test.kotlinbaseproject.service.AddService
import com.gumaoqi.test.kotlinbaseproject.tool.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyService : Service() {
    private val TAG = "MyService"
    private lateinit var gHandler: Handler
    private var running = false
    private lateinit var bd: BDLocation
    var instance = 0.0

    override fun onCreate() {
        super.onCreate()
        L.i("MyService", "onCreate")
        gHandler = Handler(Handler.Callback { msg ->
            if (running) {//已经与activity解绑了
                return@Callback false
            }
            when (msg.arg1) {
                HandlerArg.SUCCESS -> {
                }
                SERVICE_GET_LOCATION -> {
                    L.i(TAG, "获取一次地理位置")
                    val message = gHandler.obtainMessage()
                    message.arg1 = SERVICE_GET_LOCATION
                    gHandler.sendMessageDelayed(message, GuApplication.getLocationTime)
                    GuApplication.locateNow(gHandler, SERVICE_GET_LOCATION_BACK)
                }
                SERVICE_GET_LOCATION_BACK -> {
                    bd = msg.obj as BDLocation
                    val now = System.currentTimeMillis()
                    if (now - lastUploadLocationTime < uploadLocationTime) {//距离上次上传位置不足5分钟
                        return@Callback false
                    }
//                    lastUploadLocationTime = now
                    instance = if (S.getString("c7").isEmpty() || S.getString("c7") == "null") {//当前没有设置中心点
                        10000.0
                    } else {
                        C.getDistance(bd.latitude, bd.longitude,
                                S.getString("c7").toDouble(), S.getString("c8").toDouble())
                    }
                    addParamUploadLocation(bd, instance)
                }
                SERVICE_UPLOAD_LOCATION_BACK -> {
                    val addBean = msg.obj as AddBean
                    if (addBean.createdAt == null) {
                        return@Callback false
                    }
                    lastUploadLocationTime = System.currentTimeMillis()
                    L.i(TAG, "时间间隔：$uploadLocationTime")
                    setNotification("定位中。。。",
                            "上次定位时间：" + C.longTimeChangeYear(System.currentTimeMillis())
                                    + "\n上次定位位置：" + bd.addrStr
                                    + "\n上次定位距中心点距离：" + String.format("%.2f", instance) + "米"
                                    + "\n下次定位时间：" + C.longTimeChangeYear(System.currentTimeMillis() + uploadLocationTime))

                    startForeground(1, notification)
                }

            }
            false
        })
        val message = gHandler.obtainMessage()
        message.arg1 = SERVICE_GET_LOCATION
        gHandler.sendMessageDelayed(message, 100)
        setNotification("定位中。。。", "请稍后")
        startForeground(1, notification)

    }

    override fun onBind(intent: Intent?): IBinder? {
        L.i("MyService", "onBind")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     * 添加参数上传地理位置
     */
    private fun addParamUploadLocation(bdLocation: BDLocation, instance: Double) {
        val paramMap = HashMap<String, String>()
        paramMap["c1"] = "" + bdLocation.latitude
        paramMap["c2"] = "" + bdLocation.longitude
        paramMap["c3"] = bdLocation.addrStr ?: "未检测到定位"
        paramMap["c4"] = "" + System.currentTimeMillis()
        paramMap["c5"] = S.getString("c1")
        paramMap["c6"] = "" + instance
        paramMap["c7"] = Re.getMacFromHardware()
        paramMap["tablename"] = "map_location"
        uploadLocationByRetrofit(paramMap, gHandler)
    }


    /**
     * 用retrofit上传地理位置
     */
    private fun uploadLocationByRetrofit(paramMap: HashMap<String, String>, handler: Handler) {
        Re.serviceName = "f43174e44ad9f6f2/shopAddUser"
        Re.addSign(paramMap, GuApplication.context)
        val addService = Re.getRetrofit()
                .create(AddService::class.java)
        val call = addService.add(paramMap)
        L.i(TAG, "上传地理位置")
        call.enqueue(object : Callback<AddBean> {
            override fun onResponse(call: Call<AddBean>, response: Response<AddBean>?) {
                if (response?.body() == null) {
                    L.i(TAG, "上传地理位置接口返回为空")
                    T.s(getString(R.string.gu_net_error_or_server_busy))
                    return
                }
                val addBean = response.body()
                L.i(TAG, "上传地理位置接口返回:$addBean")
                val message = handler.obtainMessage()
                message.arg1 = SERVICE_UPLOAD_LOCATION_BACK
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