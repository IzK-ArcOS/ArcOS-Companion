package com.blockyheadman.arcoscompanion.data.classes

import com.google.gson.annotations.SerializedName

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