package com.singa.core.data.source.local

import com.singa.core.data.source.local.preferences.SettingPreferences
import kotlinx.coroutines.flow.Flow

class LocalDataSource(
    private val settingPreferences: SettingPreferences
) {
    fun getAccessToken(): Flow<String> = settingPreferences.getAccessToken()
    suspend fun saveAccessToken(accessToken: String) = settingPreferences.saveAccessToken(accessToken)
    suspend fun removeAccessToken() = settingPreferences.removeAccessToken()
    fun getRefreshToken(): Flow<String> = settingPreferences.getRefreshToken()
    suspend fun saveRefreshToken(refreshToken: String) = settingPreferences.saveRefreshToken(refreshToken)
    suspend fun removeRefreshToken() = settingPreferences.removeRefreshToken()
    fun getIsSecondLaunch(): Flow<Boolean> = settingPreferences.getIsSecondLaunch()
    suspend fun saveIsSecondLaunch(isSecondLaunch: Boolean) =
        settingPreferences.saveIsSecondLaunch(isSecondLaunch)
}