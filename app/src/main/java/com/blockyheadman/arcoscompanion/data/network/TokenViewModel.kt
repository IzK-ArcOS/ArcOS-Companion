package com.blockyheadman.arcoscompanion.data.network

import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class TokenData(
    val username: String,
    val token: String
)

data class TokenError(
    val title: String,
    val message: String
)

data class TokenResponse(
    val data: TokenData,
    val valid: Boolean,
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
                    Base64.encode("Blocky".toByteArray(), Base64.DEFAULT).toString() +
                        ":" +
                        Base64.encode("NiceTry".toByteArray(), Base64.DEFAULT).toString()
                )
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                //token = "${e.message}"
            }
            Log.i("GetToken", Base64.encode("Blocky".toByteArray(), Base64.DEFAULT).toString() +
                    ":" +
                    Base64.encode("NiceTry".toByteArray(), Base64.DEFAULT).toString())
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