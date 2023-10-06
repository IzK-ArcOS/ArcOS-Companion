package com.blockyheadman.arcoscompanion.data.network

import com.blockyheadman.arcoscompanion.data.classes.AuthResponse
import com.blockyheadman.arcoscompanion.data.classes.MessageList
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface APIService {
    @GET("/auth")
    suspend fun getAuth(
        @Header("authorization") auth: String,
        @Query("ac") authCode: String?
    ): AuthResponse

    @GET("messages/list")
    suspend fun getMessageList(
        @Header("authorization") auth: String,
        @Query("ac") authCode: String
    ): MessageList

    @GET("logoff")
    fun deAuth(
        @Header("authorization") auth: String,
        @Query("ac") authCode: String
    ): String

    companion object {
        private var apiService: APIService? = null
        fun getInstance(apiName: String): APIService {
            apiService = Retrofit.Builder()
                .baseUrl("https://$apiName")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(APIService::class.java)

            return apiService!!
        }
    }
}