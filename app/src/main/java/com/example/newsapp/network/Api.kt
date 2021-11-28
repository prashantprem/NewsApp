package com.example.newsapp.network

import com.example.newsapp.model.Article
import com.example.newsapp.model.newsModel
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("top-headlines/")
    fun getTopHeadlines(@Query("country") country: String, @Query("apiKey") key: String): Call<newsModel>
    }
