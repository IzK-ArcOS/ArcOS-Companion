package com.blockyheadman.arcoscompanion.data.network

import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch

data class TokenData(
    @SerializedName(value = "username")
    val username: String,
    @SerializedName(value = "token")
    val token: String
)

data class TokenError(
    @SerializedName(value = "title")
    val title: String,
    @SerializedName(value = "message")
    val message: String
)

data class TokenResponse(
    @SerializedName(value = "data")
    val data: TokenData,
    @SerializedName(value = "valid")
    val valid: Boolean,
    @SerializedName(value = "error")
    val error: TokenError
)

class TokenViewModel : ViewModel() {
    var token: TokenResponse? = null
    var errorMessage: String by mutableStateOf("")

    fun getToken(apiName: String) {
        viewModelScope.launch {
            val apiService = APIService.getInstance(apiName)
            try {
                token = apiService.getAuth(
                    "Basic " + Base64.encodeToString("Blocky:BlockyArcOS#23".toByteArray(), Base64.NO_WRAP)
                )
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                //token = "${e.message}"
            }
            Log.i("GetToken", "Basic " + Base64.encodeToString("Blocky:BlockyArcOS#23".toByteArray(), Base64.NO_WRAP) )
        }
    }
}
/*class TokenViewModel : ViewModel() {
    private val _token = mutableStateListOf<String>()
    var errorMessage: String by mutableStateOf("")
    val tokenList: List<String>
        get() = _token

    fun getTodoList() {
        viewModelScope.launch {
            val apiService = APIService.getInstance()
            try {
                _token.clear()
                _token.addAll(listOf(apiService.getAuth()))

            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }
    }
}*/