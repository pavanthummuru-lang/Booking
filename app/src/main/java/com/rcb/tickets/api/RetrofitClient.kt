package com.rcb.tickets.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://rcbscaleapi.ticketgenie.in/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("accept", "application/json, text/plain, */*")
                .addHeader("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
                .addHeader("origin", "https://shop.royalchallengers.com")
                .addHeader("referer", "https://shop.royalchallengers.com/")
                .addHeader("user-agent", "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 Chrome/147.0.0.0 Mobile Safari/537.36")
                .build()
            chain.proceed(request)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
