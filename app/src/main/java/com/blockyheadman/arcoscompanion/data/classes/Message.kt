package com.blockyheadman.arcoscompanion.data.classes

import com.google.gson.annotations.SerializedName

data class MessageList(
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
    val timestamp: Long,
    @SerializedName("replyingTo")
    val replyingTo: Int?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("read")
    val read: Boolean
)