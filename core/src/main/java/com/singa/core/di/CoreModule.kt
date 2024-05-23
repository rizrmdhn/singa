package com.singa.core.di

import com.singa.core.BuildConfig
import com.singa.core.data.SingaRepository
import com.singa.core.data.source.local.LocalDataSource
import com.singa.core.data.source.local.preferences.SettingPreferences
import com.singa.core.data.source.local.preferences.dataStore
import com.singa.core.data.source.remote.RemoteDataSource
import com.singa.core.data.source.remote.network.ApiService
import com.singa.core.domain.repository.ISingaRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val preferencesModule = module {
    single {
        SettingPreferences(androidContext().dataStore)
    }
}

val networkModule = module {
    single {
        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            val token = runBlocking { get<SettingPreferences>().getAccessToken().first() }
            val requestHeader = req.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(requestHeader)
        }

        OkHttpClient.Builder()
            .addInterceptor(
                if (BuildConfig.APP_MODE) HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                else HttpLoggingInterceptor().setLevel(
                    HttpLoggingInterceptor.Level.NONE
                )
            )
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .build()
    }
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get<OkHttpClient>())
            .build()
        retrofit.create(ApiService::class.java)
    }
}

val repositoryModule = module {
    single { LocalDataSource(get()) }
    single { RemoteDataSource(get()) }
    single<ISingaRepository> {
        SingaRepository(get(), get())
    }
}