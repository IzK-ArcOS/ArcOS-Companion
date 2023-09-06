package com.blockyheadman.arcoscompanion.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("UserPreferences")
        private val THEME_MODE = intPreferencesKey("theme_mode")
        private val MATERIAL_MODE = booleanPreferencesKey("material_mode")
    }

    val getThemeMode: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[THEME_MODE] ?: 0
    }

    suspend fun saveThemeMode(option: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = option
        }
    }

    val getMaterialYouMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[MATERIAL_MODE] ?: false
    }

    suspend fun saveMaterialYouMode(option: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[MATERIAL_MODE] = option
        }
    }
}