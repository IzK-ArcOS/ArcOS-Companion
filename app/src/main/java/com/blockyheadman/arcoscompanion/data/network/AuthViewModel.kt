package com.blockyheadman.arcoscompanion.data.network

import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch

data class AuthData(
    @SerializedName(value = "username")
    val username: String,
    @SerializedName(value = "token")
    val token: String
)

data class AuthError(
    @SerializedName(value = "title")
    val title: String,
    @SerializedName(value = "message")
    val message: String
)

data class AuthResponse(
    @SerializedName(value = "data")
    val data: AuthData,
    @SerializedName(value = "valid")
    val valid: Boolean,
    @SerializedName(value = "error")
    val error: AuthError
)

class AuthViewModel : ViewModel() {
    var auth: AuthResponse? = null
    var errorMessage: String by mutableStateOf("")

    fun getToken(apiName: String, username: String, password: String) {
        viewModelScope.launch {
            val apiService = APIService.getInstance(apiName)
            try {
                val json = Gson().toJson(apiService.getAuth(
                    "Basic " + Base64.encodeToString("$username:$password".toByteArray(), Base64.NO_WRAP)
                ))
                Log.d("JSONOutput", json)
                auth = Gson().fromJson(json, AuthResponse::class.java)
                Log.d("AuthOutput", auth.toString())
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                //token = "${e.message}"
            }
        }
    }
}