package com.test.myapplication

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.SimpleTimeZone

interface ApiInterface {
    @GET("forecast")
    fun getWeatherData(@Query("q") city: String,
                       @Query("appid") appid:String, @Query("units") units:String,): Call<Example>
}