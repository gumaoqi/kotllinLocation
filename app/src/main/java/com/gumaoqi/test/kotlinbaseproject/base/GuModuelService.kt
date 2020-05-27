package com.gumaoqi.test.kotlinbaseproject.base

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.HashMap

interface GuModuelService {
    @FormUrlEncoded
    @POST("123456/abcdefg")
    fun serviceMothed(@FieldMap paramMap: HashMap<String, String>): Call<ResponseBody>
}