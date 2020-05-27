package com.gumaoqi.test.kotlinbaseproject.tool

import android.content.Context
import com.google.gson.Gson
import com.gumaoqi.test.kotlinbaseproject.tool.I.Companion.baseUrl
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.NetworkInterface
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.experimental.and

object Re {
    private const val TAG = "GuRetrofitUtils"
    private val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(I.netTimeOut, TimeUnit.SECONDS)
            .readTimeout(I.netTimeOut, TimeUnit.SECONDS)
            .writeTimeout(I.netTimeOut, TimeUnit.SECONDS)
            .build()//创建使用的okHttp类
    var serviceName = ""

    /**
     * 获取Retrofit的默认地址接口的实列
     *
     * @return Retrofit的实列
     */
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .client(okHttpClient)
                .baseUrl(I.baseUrl)//接口的默认地址
                .build()
    }

    /**
     * 获取当前的时间
     *
     * @return 当前的时间 yyyyMMddHHmmss格式
     */
    fun getTimeSpace(): String {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        return ("" + calendar.get(Calendar.YEAR) //年

                + (if (month >= 10) "" + month else "0$month") //月

                + (if (calendar.get(Calendar.DAY_OF_MONTH) >= 10) "" + calendar.get(Calendar.DAY_OF_MONTH) else "0" + calendar.get(Calendar.DAY_OF_MONTH)) //日

                + (if (calendar.get(Calendar.HOUR_OF_DAY) >= 10) "" + calendar.get(Calendar.HOUR_OF_DAY) else "0" + calendar.get(Calendar.HOUR_OF_DAY)) //时

                + (if (calendar.get(Calendar.MINUTE) >= 10) "" + calendar.get(Calendar.MINUTE) else "0" + calendar.get(Calendar.MINUTE)) //分

                + if (calendar.get(Calendar.SECOND) >= 10) "" + calendar.get(Calendar.SECOND) else "0" + calendar.get(Calendar.SECOND))
    }

    /**
     * 获取当前的时间
     *
     * @return 当前的时间 yyyy-MM-dd HH:mm:ss格式
     */
    fun getFormatTime(): String {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        return ("" + calendar.get(Calendar.YEAR) + "-"
                + (if (month >= 10) "" + month else "0$month") + "-"
                + (if (calendar.get(Calendar.DAY_OF_MONTH) >= 10) "" + calendar.get(Calendar.DAY_OF_MONTH) else "0" + calendar.get(Calendar.DAY_OF_MONTH)) + "  "
                + (if (calendar.get(Calendar.HOUR_OF_DAY) >= 10) "" + calendar.get(Calendar.HOUR_OF_DAY) else "0" + calendar.get(Calendar.HOUR_OF_DAY)) + ":"
                + (if (calendar.get(Calendar.MINUTE) >= 10) "" + calendar.get(Calendar.MINUTE) else "0" + calendar.get(Calendar.MINUTE)) + ":"
                + if (calendar.get(Calendar.SECOND) >= 10) "" + calendar.get(Calendar.SECOND) else "0" + calendar.get(Calendar.SECOND))
    }


    /**
     * 获取当前的时间，仅年月日
     *
     * @return 当前的时间 yyyy-MM-dd 格式
     */
    fun getFormatTimeOnlyYearMonthDay(): String {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        return ("" + calendar.get(Calendar.YEAR) + "-"
                + (if (month >= 10) "" + month else "0$month") + "-"
                + (if (calendar.get(Calendar.DAY_OF_MONTH) >= 10) "" + calendar.get(Calendar.DAY_OF_MONTH) else "0" + calendar.get(Calendar.DAY_OF_MONTH)) + "")
    }

    /**
     * 获取当前的时间，仅年月
     *
     * @return 当前的时间 yyyy-MM格式
     */
    fun getFormatTimeOnlyYearMonth(): String {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        return "" + calendar.get(Calendar.YEAR) + "-" +
                (if (month >= 10) "" + month else "0$month") + ""
    }

    /**
     * 获取当前的时间，仅年和上个月
     *
     * @return 当前的时间 yyyy-MM格式
     */
    fun getFormatTimeOnlyYearLastMonth(): String {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        return "" + calendar.get(Calendar.YEAR) + "-" +
                (if (month - 1 >= 10) "" + (month - 1) else "0" + (month - 1)) + ""
    }

    /**
     * 加签，中文处理方式为URLEncode。保证访问服务器的安全
     * 例子：四川省成都市锦江区---%e5%9b%9b%e5%b7%9d%e7%9c%81%e6%88%90%e9%83%bd%e5%b8%82%e9%94%a6%e6%b1%9f%e5%8c%ba
     *
     * @param map
     * @param context
     */
    fun addSign(map: HashMap<String, String>, context: Context) {
        //将map里面除了sign和base64的参数保存到list数组中
        var strings: MutableList<String> = ArrayList()
        for (key in map.keys) {
            if (key == "sign") {//签名不加
                continue
            }
            if (key == "base64") {//base64图片数据不加
                continue
            }
            if (map[key] == null) {
                L.i(TAG, key)
            }
            strings.add(("&" + key + "=" + URLEncoder.encode(map[key])).toLowerCase())
        }
        //进行排序
        strings.sort()
//        Collections.sort(strings) { o1, o2 -> o1.compareTo(o2) }
        //拿到排序后字符串str
        var str = ""
        for (string in strings) {
            str += string
        }
        //将字符串中的大写字母全部变为小写
        if (str.isNotEmpty()) {
            str = str.substring(1).toLowerCase()
        }
        //获取sign并添加到map中
        var sign: String = ""
        sign = getMD5String(str)
        L.i(TAG, "验签前的字符串：$str")
        L.i(TAG, "生成的验签：$sign")
//        map["sign"] = sign

        //重新获取添加sign
        strings = ArrayList()
        for (key in map.keys) {
            if (key == "base64") {//base64图片数据不加
                continue
            }
            strings.add(("&" + key + "=" + map[key]).toLowerCase())
        }
        //进行排序
        strings.sort()
//        Collections.sort(strings) { o1, o2 -> o1.compareTo(o2) }
        //拿到排序后字符串str
        str = ""
        for (string in strings) {
            str += string
        }
        //将字符串中的大写字母全部变为小写
        if (str.isNotEmpty()) {
            str = str.substring(1).toLowerCase()
        }
        str = "$baseUrl$serviceName?$str"
        L.i(TAG + "具体访问的地址", str)
        return
    }

    /**
     * 中文转gb2321
     *
     * @param string
     * @return 字符串对应的gb2321
     */
    fun stringToUnicode(string: String): String {
        val unicode = StringBuffer()
        for (i in 0 until string.length) {
            // 取出每一个字符
            val c = string[i]
            // 转换为unicode
            //"\\u只是代号，请根据具体所需添加相应的符号"
            unicode.append("%u" + Integer.toHexString(c.toInt()))
        }
        return unicode.toString()
    }

    /**
     * 判断一个字符是否是汉字
     * PS：中文汉字的编码范围：[\u4e00-\u9fa5]
     *
     * @param c 需要判断的字符
     * @return 是汉字(true), 不是汉字(false)
     */
    fun isChineseChar(c: Char): Boolean {
        return c.toString().matches("[\u4e00-\u9fa5]".toRegex())
    }

    /**
     * 获取指定字符串md5的值
     *
     * @param value 指定的字符串
     * @return
     */
    fun getMD5String(value: String): String {

        return if (value.isEmpty()) "" else getMD5String(value.toByteArray())
    }

    /**
     * 获取指定bytes数组md5的值
     *
     * @param bytes 指定bytes数组
     * @return
     */
    fun getMD5String(bytes: ByteArray): String {

        var result: String = ""

        var messageDigest: MessageDigest? = null
        try {
            messageDigest = MessageDigest.getInstance("MD5")
            if (messageDigest != null && bytes.isNotEmpty()) {
                messageDigest.update(bytes)
                result = bytes2HexLowerCaseString(messageDigest.digest())
            }
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return result
    }

    /**
     * 将指定的byte数组转换为小写
     *
     * @param b 指定的byte数组
     * @return
     */
    fun bytes2HexLowerCaseString(b: ByteArray): String {
        var ret = ""
        for (i in b.indices) {
            var hex = Integer.toHexString((b[i] and 0xFF.toByte()).toInt())
            if (hex.length == 1) {
                hex = "0$hex"
            }
            ret += hex.toLowerCase()
        }
        return ret
    }

    /**
     * 获取机器的mac地址
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET"></uses-permission>
     *
     * @return
     */
    fun getMacFromHardware(): String {
        try {
            val all = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (!nif.name.equals("wlan0", ignoreCase = true)) continue

                val macBytes = nif.hardwareAddress ?: return ""

                val res1 = StringBuilder()
                for (b in macBytes) {
                    res1.append(String.format("%02X:", b))
                }

                if (res1.length > 0) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "02:00:00:00:00:00"
    }

    /**
     * 通过请求参数构造RquestBody
     * 与c#组交互的时候使用，不知道为什么与java组交互的时候就不使用了。
     *
     * @param paramMap 请求参数的map
     * @return
     */
    fun getRquestBody(paramMap: HashMap<String, String>): RequestBody {
        var string = "{"
        val stringList = ArrayList<String>()
        for (str in paramMap.keys) {
            stringList.add(str)
        }
        stringList.sort()
        for (str in stringList) {
            string += "\"" + str + "\":" + "\"" + paramMap[str] + "\","
        }
        string = string.substring(0, string.length - 1)
        string += "}"
        val body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), string)
        L.i(TAG, string)
        return body
    }
}