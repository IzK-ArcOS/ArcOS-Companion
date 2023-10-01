package com.blockyheadman.arcoscompanion.data

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("vaild")
    val valid: Boolean,
    @SerializedName("data")
    val data: List<MessageData>
)

data class MessageData(
    @SerializedName("sender")
    val sender: String,
    @SerializedName("receiver")
    val receiver: String,
    @SerializedName("partialBody")
    val partialBody: String,
    @SerializedName("timestamp")
    val timestamp: Int,
    @SerializedName("replyingTo")
    val replyingTo: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("read")
    val read: Boolean
)