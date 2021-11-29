package com.qonita.newsappapplication.service

import com.qonita.newsappapplication.model.ResponseNews
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines")
    fun getNewsHeadLines(
        @Query("country") country: String?,
        @Query("apiKey") apiKey: String?
    ): Call<ResponseNews>
}