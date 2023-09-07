package com.blockyheadman.arcoscompanion.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

interface APIService {
    //@Headers("Authorization: Blocky:BlockyArcOS#23")
    @GET("/auth")
    suspend fun getAuth(@Header("authorization") auth: String): TokenResponse

    companion object {
        var apiService: APIService? = null
        fun getInstance(apiName: String): APIService {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl("https://${apiName}.arcapi.nl")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(APIService::class.java)
            }
            return apiService!!
        }
    }
}