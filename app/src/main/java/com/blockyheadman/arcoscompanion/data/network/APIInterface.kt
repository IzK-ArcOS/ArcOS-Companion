package com.blockyheadman.arcoscompanion.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

interface APIService {
    @GET("/auth")
    suspend fun getAuth(@Header("authorization") auth: String): AuthResponse

    companion object {
        private var apiService: APIService? = null
        fun getInstance(apiName: String): APIService {
            apiService = Retrofit.Builder()
                .baseUrl("https://${apiName}")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(APIService::class.java)
            return apiService!!
        }
    }
}