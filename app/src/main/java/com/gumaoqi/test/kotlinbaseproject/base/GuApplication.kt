package com.gumaoqi.test.kotlinbaseproject.base

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import androidx.core.app.NotificationCompat
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.model.LatLng
import com.gumaoqi.test.kotlinbaseproject.LoadingActivity
import com.gumaoqi.test.kotlinbaseproject.R
import com.gumaoqi.test.kotlinbaseproject.tool.C
import com.gumaoqi.test.kotlinbaseproject.tool.L

class GuApplication : Application() {

    companion object {
        private const val TAG = "GuApplication"
        lateinit var context: Context
        lateinit var notification: Notification//前台服务的通知
        const val loginEffectTime = 1000 * 60 * 60 * 24//登录有效时间
        const val maxPicSize = 9740516//选中照片最大大小为6m
        const val clickEffectTime = 1000 * 1//点击有效时间，防止重复点击
        const val getLocationTime = (1000 * 60 * 1) as Long//每1分钟获取一次地理位置
        const val uploadLocationTime = (1000 * 60 * 5) as Long//每5分钟上传一次地理位置
        var lastUploadLocationTime = 0L//上一次上传位置时间
        var lastClickTime = System.currentTimeMillis()//上一次点击事件

        fun setNotification(title: String, content: String) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//Android版本大于android O需要使用到频道
                val channel = NotificationChannel("my_service", "前台Service通知", NotificationManager.IMPORTANCE_DEFAULT)
                manager.createNotificationChannel(channel)
            }
            val intent = Intent(context, LoadingActivity::class.java)
            val pi = PendingIntent.getActivity(context, 0, intent, 0)
            notification = NotificationCompat.Builder(context, "my_service")
                    .setContentTitle(title)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(content))
//                    .setContentText(content)
                    .setSmallIcon(R.mipmap.ico_arrowright)
                    .setContentIntent(pi)
                    .build()
        }

        //百度地图所使用的类
        lateinit var mLocationClient: LocationClient
        lateinit var option: LocationClientOption
        lateinit var bdAbstractLocationListener: BDAbstractLocationListener

        /**
         * 发起定位
         * @param handler
         * @param arg1
         */
        fun locateNow(handler: Handler, arg1: Int) {
            bdAbstractLocationListener = object : BDAbstractLocationListener() {
                override fun onReceiveLocation(bdLocation: BDLocation) {
                    L.i("UploadLocationService", "发送了一次定位")
                    if (bdLocation.addrStr.isEmpty()) {
                        bdLocation.addrStr = "定位失败"
                    }
                    val message = handler.obtainMessage()
                    message.arg1 = arg1
                    message.obj = bdLocation
                    handler.sendMessage(message)
                    mLocationClient.unRegisterLocationListener(bdAbstractLocationListener)
                    mLocationClient.stop()
                }
            }

            mLocationClient.registerLocationListener(bdAbstractLocationListener)
            mLocationClient.start()
        }

        /**
         * 将地图移动到指定的location
         *
         * @param weiDu
         * @param jingDu
         * @param duration
         * @param baiduMap
         */
        fun centerToLocation(weiDu: Double, jingDu: Double, duration: Int, baiduMap: BaiduMap) {
            val cenpt = LatLng(weiDu, jingDu)
            //定义地图状态,地图缩放级别 3~19
            val newMapStatus = MapStatus.Builder().target(cenpt).zoom(16f).build()
            //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
            val mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(newMapStatus)
            //改变地图状态
            baiduMap.animateMapStatus(mMapStatusUpdate, duration)
        }
    }


    override fun onCreate() {
        super.onCreate()
        initBaiDuMap()
        context = this
        L.i(TAG, "onCreate")
    }

    override fun onTerminate() {
        super.onTerminate()
        L.i(TAG, "onTerminate")
        ActivityCollector.finishAll()
    }

    /**
     * 初始化百度地图
     */
    private fun initBaiDuMap() {
        mLocationClient = LocationClient(applicationContext)
        option = LocationClientOption()
        option.setIsNeedLocationDescribe(true)
        option.setIsNeedAddress(true)
        option.setIsNeedLocationPoiList(true)
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true

        option.isOpenGps = true // 打开gps
        option.setCoorType("bd09ll") // 设置坐标类型
        option.setScanSpan(1000)
        mLocationClient.locOption = option

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this)
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL)
    }

}