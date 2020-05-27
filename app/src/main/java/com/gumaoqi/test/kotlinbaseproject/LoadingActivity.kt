package com.gumaoqi.test.kotlinbaseproject

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.gumaoqi.test.kotlinbaseproject.base.BaseActivity
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg
import com.gumaoqi.test.kotlinbaseproject.fragment.LoadingFragment
import com.gumaoqi.test.kotlinbaseproject.location.MyService
import com.gumaoqi.test.kotlinbaseproject.tool.I
import com.gumaoqi.test.kotlinbaseproject.tool.T
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.title.*

class LoadingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        iniData()
        setLayout()
    }

    override fun onResume() {
        super.onResume()
        setView()
    }

    private fun iniData() {

    }

    private fun setLayout() {
        title_root_ll.visibility = View.GONE
        PermissionX.init(this)
                .permissions(
//                        Manifest.permission.CALL_PHONE,//打电话的权限
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE, //对存储的读写
//                        Manifest.permission.CAMERA, //相机
                        Manifest.permission.ACCESS_FINE_LOCATION //精确位置
//                        Manifest.permission.RECORD_AUDIO, //麦克风
//                        Manifest.permission.ACCESS_COARSE_LOCATION//大致位置
                )
                .onExplainRequestReason { deniedList ->
                    showRequestReasonDialog(deniedList, "即将重新申请的权限是程序必须依赖的权限", "我已明白", "取消")
                }
                .onForwardToSettings { deniedList ->
                    showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "我已明白", "取消")
                }
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        changeFragment(R.id.activity_main_content_fl, LoadingFragment())
                    } else {
                        T.s("您拒绝了某些必要的权限")
                        finish()
                    }
                }

    }


    private fun setView() {

    }

    override fun onBackPressed() {
    }
}