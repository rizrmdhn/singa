package com.singa.asl

import android.app.Application
import com.singa.asl.di.useCaseModule
import com.singa.asl.di.viewModelModule
import com.singa.core.di.networkModule
import com.singa.core.di.preferencesModule
import com.singa.core.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MyApplication)
            modules(
                listOf(
                    preferencesModule,
                    networkModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModule
                )
            )
        }
    }
}