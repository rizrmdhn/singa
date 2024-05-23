package com.singa.core.domain.repository

import com.singa.core.data.Resource
import com.singa.core.domain.model.RefreshToken
import com.singa.core.domain.model.Token
import com.singa.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ISingaRepository {
    fun register(email: String, password: String): Flow<Resource<String>>

    fun login(email: String, password: String): Flow<Resource<Token>>

    fun logout(): Flow<Resource<String>>

    fun guest(): Flow<Resource<Token>>

    fun getMe(): Flow<Resource<User>>

    fun updateMe(name: String, email: String, avatar: String): Flow<Resource<User>>

    fun updateToken(refreshToken: String): Flow<Resource<RefreshToken>>

    fun getAccessToken(): Flow<String>

    suspend fun saveAccessToken(token: String)

    suspend fun removeAccessToken()

    fun getRefreshToken(): Flow<String>

    suspend fun saveRefreshToken(token: String)

    suspend fun removeRefreshToken()

    fun getIsSecondLaunch(): Flow<Boolean>

    suspend fun saveIsSecondLaunch(isFirstLaunch: Boolean)
}