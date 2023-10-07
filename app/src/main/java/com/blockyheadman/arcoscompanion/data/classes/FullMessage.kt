package com.blockyheadman.arcoscompanion.data.classes

import com.google.gson.annotations.SerializedName

data class FullMessage(
    @SerializedName("valid")
    val valid: Boolean,
    @SerializedName("data")
    val data: FullMessageData,
)

data class FullMessageData(
    @SerializedName("sender")
    val sender: String,
    @SerializedName("receiver")
    val receiver: String,
    @SerializedName("body")
    val body: String,
    @SerializedName("replies")
    val replies: List<Int>,
    @SerializedName("replyingTo")
    val replyingTo: Int?,
    @SerializedName("timestamp")
    val timestamp: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("read")
    val read: Boolean,
)
