package com.gumaoqi.test.kotlinbaseproject.tool

/**
 * 单例类，log打印工具
 * 系统的一些配置信息
 */
class I {
    companion object {
        const val isShowLog = true//是否打印log
        const val loginEffectTime = 1000 * 60 * 60 * 24 * 30//登录失效时间为30天
        const val netTimeOut = 30L//网络最长等待时间为30秒
        const val baseUrl = "http://javacloud.bmob.cn/"//连接服务器的基础地址
    }
}