package com.gumaoqi.test.kotlinbaseproject.service

import com.gumaoqi.test.kotlinbaseproject.entity.SmsBean
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.HashMap

interface GetSmsService {
    @FormUrlEncoded
    @POST("f43174e44ad9f6f2/shopSendSms")
    fun getSms(@FieldMap paramMap: HashMap<String, String>): Call<SmsBean>
}