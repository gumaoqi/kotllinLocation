package com.gumaoqi.test.kotlinbaseproject.service

import com.gumaoqi.test.kotlinbaseproject.entity.UpdateBean
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.HashMap

interface UpdateService {
    @FormUrlEncoded
    @POST("f43174e44ad9f6f2/shopUpdateUser")
    fun update(@FieldMap paramMap: HashMap<String, String>): Call<UpdateBean>
}