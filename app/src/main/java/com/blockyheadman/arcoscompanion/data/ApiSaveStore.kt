package com.blockyheadman.arcoscompanion.data

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update

@Entity(tableName = "apis", primaryKeys = ["name", "username"])
data class ApiSaveData(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "auth_code") val authCode: String?
)

@Dao
interface ApiSaveDao {
    @Query("SELECT * FROM apis")
    suspend fun getAll(): List<ApiSaveData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(api: ApiSaveData)

    @Update
    suspend fun update(api: ApiSaveData)

    @Delete
    suspend fun delete(api: ApiSaveData)

    @Query("DELETE FROM apis")
    suspend fun deleteAll()
}

@Database(entities = [ApiSaveData::class], version = 1, exportSchema = false)
abstract class ApiSaveDatabase : RoomDatabase() {
    abstract fun apiSaveDao(): ApiSaveDao

    companion object {
        private var INSTANCE: ApiSaveDatabase? = null

        fun getInstance(context: Context): ApiSaveDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ApiSaveDatabase::class.java,
                        "api_database"
                    ).build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}