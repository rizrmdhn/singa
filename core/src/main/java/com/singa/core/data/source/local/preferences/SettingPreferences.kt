package com.singa.core.data.source.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.singa.core.common.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.SETTINGS)

class SettingPreferences(
    private val dataStore: DataStore<Preferences>
) {
    private val isSecondLaunchKey = booleanPreferencesKey(Constants.IS_SECOND_LAUNCH_KEY)
    private val accessTokenKey = stringPreferencesKey(Constants.ACCESS_TOKEN_KEY)
    private val refreshTokenKey = stringPreferencesKey(Constants.REFRESH_TOKEN_KEY)

    fun getAccessToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[accessTokenKey] ?: ""
        }
    }

    suspend fun saveAccessToken(accessToken: String) {
        dataStore.edit { preferences ->
            preferences[accessTokenKey] = accessToken
        }
    }

    suspend fun removeAccessToken() {
        dataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
        }
    }

    fun getRefreshToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[refreshTokenKey] ?: ""
        }
    }

    suspend fun saveRefreshToken(refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[refreshTokenKey] = refreshToken
        }
    }

    suspend fun removeRefreshToken() {
        dataStore.edit { preferences ->
            preferences.remove(refreshTokenKey)
        }
    }

    fun getIsSecondLaunch(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[isSecondLaunchKey] ?: false
        }
    }

    suspend fun saveIsSecondLaunch(isSecondLaunch: Boolean) {
        dataStore.edit { preferences ->
            preferences[isSecondLaunchKey] = isSecondLaunch
        }
    }
}