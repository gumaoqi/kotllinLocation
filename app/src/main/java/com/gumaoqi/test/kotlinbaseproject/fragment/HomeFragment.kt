package com.gumaoqi.test.kotlinbaseproject.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.baidu.location.BDLocation
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MyLocationData
import com.gumaoqi.test.kotlinbaseproject.R
import com.gumaoqi.test.kotlinbaseproject.base.BaseFragment
import com.gumaoqi.test.kotlinbaseproject.base.GuApplication
import com.gumaoqi.test.kotlinbaseproject.base.GuApplication.Companion.lastUploadLocationTime
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.GET_LOCATION_BACK
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.SET_CENTER_BACK
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.SUCCESS
import com.gumaoqi.test.kotlinbaseproject.entity.AddBean
import com.gumaoqi.test.kotlinbaseproject.entity.UpdateBean
import com.gumaoqi.test.kotlinbaseproject.service.AddService
import com.gumaoqi.test.kotlinbaseproject.service.UpdateService
import com.gumaoqi.test.kotlinbaseproject.tool.*
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : BaseFragment() {

    private lateinit var gHandler: Handler

    private lateinit var baiduMap: BaiduMap

    private lateinit var bd: BDLocation
    var instance = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
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
                GET_LOCATION_BACK -> {
                    L.i(TAG, "执行了一次定位")
                    fragment_home_update_tv.text = "点我获取当前位置并上传"
                    bd = msg.obj as BDLocation
                    val latitude = bd.latitude
                    val longitude = bd.longitude
                    fragment_home_tv.text = bd.addrStr
                    baiduMap = fragment_home_map.map
                    //在地图上讲我的点标识出来
                    baiduMap.isMyLocationEnabled = true
                    baiduMap.setMyLocationData(MyLocationData.Builder()
                            .latitude(latitude)
                            .longitude(longitude)
                            .build())
                    //将地图移动到定位点
                    GuApplication.centerToLocation(latitude, longitude, 1000, baiduMap)
                    instance = if (S.getString("c7").isEmpty() || S.getString("c7") == "null") {//当前没有设置中心点
                        10000.0
                    } else {
                        C.getDistance(bd.latitude, bd.longitude,
                                S.getString("c7").toDouble(), S.getString("c8").toDouble())
                    }
                    addParamUploadLocation(bd, instance)

                }
                HandlerArg.FRAGMENT_UPLOAD_LOCATION_BACK -> {
                    val addBean = msg.obj as AddBean
                    if (addBean.createdAt == null) {
                        T.s("上传位置失败，请重试")
                        return@Callback false
                    }
                    T.s("上传位置成功")
                }
                SET_CENTER_BACK -> {
                    val updateBean = msg.obj as UpdateBean
                    if (updateBean.updatedAt == null) {
                        return@Callback false
                    }
                    T.s("设置中心点成功")
                    S.setString("c4", "" + bd.latitude)
                    S.setString("c5", "" + bd.longitude)
                    S.setString("c6", "" + bd.addrStr)
                }
            }
            false
        })
    }

    override fun setView() {
        super.setView()
        fragment_home_update_tv.setOnClickListener {
            if (fragment_home_update_tv.text == "点我获取当前位置并上传")
                fragment_home_update_tv.text = "获取位置中，请稍后"
            GuApplication.locateNow(gHandler, GET_LOCATION_BACK)
        }
        if (S.getString("c10") == "1") {//当前是管理员
            fragment_home_set_center_bt.visibility = View.VISIBLE
            fragment_home_set_center_bt.setOnClickListener {
                if (!::bd.isInitialized) {
                    T.s("请先进行定位")
                    return@setOnClickListener
                }
                addParamSetCenter()
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
     * 添加参数上传地理位置
     */
    private fun addParamUploadLocation(bdLocation: BDLocation, instance: Double) {
        val paramMap = HashMap<String, String>()
        paramMap["c1"] = "" + bdLocation.latitude
        paramMap["c2"] = "" + bdLocation.longitude
        paramMap["c3"] = bdLocation.addrStr
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
                message.arg1 = HandlerArg.FRAGMENT_UPLOAD_LOCATION_BACK
                message.obj = addBean
                handler.sendMessageDelayed(message, 100)
            }

            override fun onFailure(call: Call<AddBean>, t: Throwable) {
                L.i(TAG, "进行注册接口连接超时,${t.message}")
                T.s(getString(R.string.gu_net_error_or_server_busy))
            }
        })
    }

    /**
     * 添加参数将当前位置设置为中心点
     */
    private fun addParamSetCenter() {
        val paramMap = HashMap<String, String>()
        paramMap["c4"] = "" + bd.latitude
        paramMap["c5"] = "" + bd.longitude
        paramMap["c6"] = "" + bd.addrStr
        paramMap["objectid"] = S.getString("object_id")
        paramMap["tablename"] = "shop_user"
        setCenterByRetrofit(paramMap, gHandler)
    }


    /**
     * 用retrofit将当前位置设置为中心点
     */
    private fun setCenterByRetrofit(paramMap: HashMap<String, String>, handler: Handler) {
        Re.serviceName = "f43174e44ad9f6f2/shopUpdateUser"
        Re.addSign(paramMap, GuApplication.context)
        val updateService = Re.getRetrofit()
                .create(UpdateService::class.java)
        val call = updateService.update(paramMap)
        L.i(TAG, "当前位置设置为中心点")
        call.enqueue(object : Callback<UpdateBean> {
            override fun onResponse(call: Call<UpdateBean>, response: Response<UpdateBean>?) {
                if (response?.body() == null) {
                    L.i(TAG, "当前位置设置为中心点接口返回为空")
                    T.s(getString(R.string.gu_net_error_or_server_busy))
                    return
                }
                val updateBean = response.body()
                L.i(TAG, "当前位置设置为中心点接口返回:$updateBean")
                val message = handler.obtainMessage()
                message.arg1 = SET_CENTER_BACK
                message.obj = updateBean
                handler.sendMessageDelayed(message, 100)
            }

            override fun onFailure(call: Call<UpdateBean>, t: Throwable) {
                L.i(TAG, "当前位置设置为中心点接口连接超时,${t.message}")
                T.s(getString(R.string.gu_net_error_or_server_busy))
            }
        })
    }
}