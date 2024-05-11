package com.singa.core.data.source.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.singa.core.common.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.SETTINGS)

class SettingPreferences(
    private val dataStore: DataStore<Preferences>
) {
    private val isSecondLaunchKey = booleanPreferencesKey(Constants.IS_SECOND_LAUNCH_KEY)

    fun getIsSecondLaunch(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[isSecondLaunchKey] ?: true
        }
    }

    suspend fun saveIsSecondLaunch(isSecondLaunch: Boolean) {
        dataStore.edit { preferences ->
            preferences[isSecondLaunchKey] = isSecondLaunch
        }
    }
}