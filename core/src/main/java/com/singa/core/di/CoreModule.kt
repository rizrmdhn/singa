package com.singa.core.di

import com.singa.core.data.SingaRepository
import com.singa.core.data.source.local.LocalDataSource
import com.singa.core.data.source.local.preferences.SettingPreferences
import com.singa.core.data.source.local.preferences.dataStore
import com.singa.core.domain.repository.ISingaRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val preferencesModule = module {
    single {
        SettingPreferences(androidContext().dataStore)
    }
}

val repositoryModule = module {
    single { LocalDataSource(get()) }
    single<ISingaRepository> {
        SingaRepository(get())
    }
}