package com.singa.core.data.source.local

import com.singa.core.data.source.local.preferences.SettingPreferences
import kotlinx.coroutines.flow.Flow

class LocalDataSource(
    private val settingPreferences: SettingPreferences
) {
    fun getIsSecondLaunch(): Flow<Boolean> = settingPreferences.getIsSecondLaunch()

    suspend fun saveIsSecondLaunch(isSecondLaunch: Boolean) =
        settingPreferences.saveIsSecondLaunch(isSecondLaunch)
}