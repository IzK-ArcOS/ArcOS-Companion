package com.blockyheadman.arcoscompanion.data.network

import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.blockyheadman.arcoscompanion.data.MessageList
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

data class AuthData(
    @SerializedName("username")
    val username: String,
    @SerializedName("token")
    val token: String
)

data class AuthError(
    @SerializedName("title")
    val title: String,
    @SerializedName("message")
    val message: String
)

data class AuthResponse(
    @SerializedName("data")
    val data: AuthData,
    @SerializedName("valid")
    val valid: Boolean,
    @SerializedName("error")
    val error: AuthError
)

class ApiCall {
    var errorMessage: String by mutableStateOf("")

    suspend fun getToken(apiName: String, username: String, password: String, authCode: String?): AuthResponse? {
        //val apiService = APIService.getInstance(apiName, authCode)
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
}