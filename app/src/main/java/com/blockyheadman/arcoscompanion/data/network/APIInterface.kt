package com.blockyheadman.arcoscompanion.data.network

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface APIService {
    @GET("/auth")
    suspend fun getAuth(@Header("authorization") auth: String, @Query("ac") authCode: String?): AuthResponse

    companion object {
        private var apiService: APIService? = null
        fun getInstance(apiName: String, authCode: String?): APIService {
            Log.d("APIAUTHCODE", "'$authCode'")
            /*apiService = if (!authCode.isNullOrBlank()) { //authCode.isNullOrBlank()
                Log.d("ApiReq", "AuthCode Request sent.")
                Log.d("ApiReq", "https://$apiName?ac=$authCode") // add "../?ac=.."
                Retrofit.Builder()
                    .baseUrl("https://$apiName?ac=$authCode")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(APIService::class.java)
            } else {
                Log.d("ApiReq", "Regular Request sent.")
                Retrofit.Builder()
                    .baseUrl("https://$apiName")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(APIService::class.java)
            }*/

            apiService = Retrofit.Builder()
                .baseUrl("https://$apiName")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(APIService::class.java)

            return apiService!!
        }
    }
}