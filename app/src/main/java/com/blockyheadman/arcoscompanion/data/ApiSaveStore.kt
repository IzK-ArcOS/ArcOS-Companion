package com.blockyheadman.arcoscompanion.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query

/*import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.google.gson.annotations.SerializedName
import java.io.InputStream
import java.io.OutputStream*/

@Entity(tableName = "apis")
data class ApiSaveData(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "auth_code") val authCode: String?,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "password") val password: String
)

@Dao
interface ApiSaveDao {
    @Query("SELECT * FROM apis")
    fun getAll(): List<ApiSaveData>
}

/*data class ApiList(
    @SerializedName("ApiList")
    val list: List<ApiSave>
)

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

class ApiSaveCall {
    //var errorMessage: String by mutableStateOf("")

    suspend fun addAPI(
        context: Context,
        name: String,
        authCode: String?,
        username: String,
        password: String
    ) {
        if (authCode != null) {
            context.apiDataStore.updateData { currentData ->
                currentData.toBuilder()
                    .setName(name)
                    .setAuthCode(authCode)
                    .setUsername(username)
                    .setPassword(password)
                    .build()
            }
        } else {
            context.apiDataStore.updateData { currentData ->
                currentData.toBuilder()
                    .setName(name)
                    .setUsername(username)
                    .setPassword(password)
                    .build()
            }
        }
    }
}*/

/*class ApiSaveIO {
    var errorMessage: String by mutableStateOf("")

    suspend fun getApiSaveList(context: Context): List<ApiSave>? {
        var apiSaveList: List<ApiSave>? = null
        try {
            coroutineScope {
                launch {
                    apiSaveList = context.apiDataStore.data
                        .catch { exception ->
                            if (exception is IOException) {
                                Log.e(
                                    "ApiSaveFlow",
                                    "Error reading sort order preferences.",
                                    exception
                                )
                                emit(ApiSave.getDefaultInstance())
                            } else {
                                throw exception
                            }
                        }
                        .toList()
                        return@launch apiSaveList
                }.await()
                apiSaveList?.forEach {
                    Log.d("GetApiSaveList", it.name)
                    Log.d("GetApiSaveList", it.authCode)
                    Log.d("GetApiSaveList", it.username)
                    Log.d("GetApiSaveList", it.password)
                }
            }
        } catch (e: Exception) {
            errorMessage = e.message.toString()
        }
        return null
    }
}*/