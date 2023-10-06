package com.blockyheadman.arcoscompanion.data.network

import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.blockyheadman.arcoscompanion.data.classes.AuthResponse
import com.blockyheadman.arcoscompanion.data.classes.MessageList
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

class ApiCall {
    var errorMessage: String by mutableStateOf("")

    suspend fun getToken(
        apiName: String,
        username: String,
        password: String,
        authCode: String?
    ): AuthResponse? {
        var auth: AuthResponse? = null
        try {
            auth = runBlocking {
                async(Dispatchers.IO) {
                    val apiService = APIService.getInstance(apiName)
                    val json = Gson().toJson(
                        apiService.getAuth(
                            "Basic " + Base64.encodeToString(
                                "$username:$password".toByteArray(),
                                Base64.NO_WRAP
                            ),
                            authCode
                        )
                    )

                    Log.d("JSONOutput", json)
                    val data = Gson().fromJson(json, AuthResponse::class.java)
                    Log.d("AuthOutput", data.toString())
                    return@async data
                }
            }.await()
        } catch (e: Exception) {
            errorMessage = e.message.toString()
        }

        return auth
    }

    suspend fun getMessages(apiName: String, authCode: String, auth: String): MessageList? {
        var data: MessageList? = null

        try {
            coroutineScope {
                async {
                    val apiService = APIService.getInstance(apiName)
                    val json = Gson().toJson(
                        apiService.getMessageList(
                            "Bearer $auth",
                            authCode
                        )
                    )

                    Log.d("JSONOutput", json)
                    data = Gson().fromJson(json, MessageList::class.java)
                    Log.d("MessageOutput", data.toString())
                }.await()
            }
        } catch (e: Exception) {
            errorMessage = e.message.toString()
        }

        return data
    }

    suspend fun deAuthToken(apiName: String, authCode: String, auth: String) {
        try {
            coroutineScope {
                async {
                    val apiService = APIService.getInstance(apiName)
                    val json = Gson().toJson(
                        apiService.deAuth(
                            "Bearer $auth",
                            authCode
                        )
                    )

                    Log.d("JSONOutput", json)
                    val data = Gson().fromJson(json, String::class.java)
                    Log.d("DeAuthOutput", data.toString())
                }
            }.await()
        } catch (e: Exception) {
            errorMessage = e.message.toString()
        }
    }

    /*fun deAuthToken(apiName: String, authCode: String, auth: String) {
        try {
            val apiService = APIService.getInstance(apiName)
            apiService.deAuth("Bearer $auth", authCode)
            Log.e("DeAuth", "De-authentication successful!")
        } catch (e: Exception) {
            Log.e("DeAuthError", e.message.toString())
        }
    }*/
}