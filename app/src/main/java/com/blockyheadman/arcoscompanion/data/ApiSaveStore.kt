package com.blockyheadman.arcoscompanion.data

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object ApiSaveSerializer: Serializer<ApiSave> {
    override val defaultValue: ApiSave = ApiSave.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ApiSave {
        try {
            return ApiSave.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: ApiSave,
        output: OutputStream
    ) = t.writeTo(output)

}

val Context.apiDataStore: DataStore<ApiSave> by dataStore(
    fileName = "apisave.pb",
    serializer = ApiSaveSerializer
)