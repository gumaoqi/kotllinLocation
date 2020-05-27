package com.gumaoqi.test.kotlinbaseproject.tool

/**
 * 单例类，log打印工具
 * 系统的一些配置信息
 */
class I {
    companion object {
        const val isShowLog = true
        const val loginEffectTime = 1000 * 60 * 60 * 24
        const val netTimeOut = 30L
        const val baseUrl = "http://javacloud.bmob.cn/"//连接服务器的基础地址
    }
}