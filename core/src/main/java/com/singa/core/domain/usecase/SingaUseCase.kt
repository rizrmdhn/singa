package com.singa.core.domain.usecase

import com.singa.core.data.Resource
import com.singa.core.domain.model.RefreshToken
import com.singa.core.domain.model.Token
import com.singa.core.domain.model.User
import kotlinx.coroutines.flow.Flow
import java.io.File

interface SingaUseCase {
    fun register(name: String, email: String, password: String): Flow<Resource<String>>

    fun login(email: String, password: String): Flow<Resource<Token>>

    fun logout(): Flow<Resource<String>>

    fun guest(): Flow<Resource<Token>>

    fun getMe(): Flow<Resource<User>>

    fun updateMe(
        name: String?,
        password: String?,
        avatar: File?,
        isSignUser: Boolean?
    ): Flow<Resource<User>>

    fun updateToken(): Flow<Resource<RefreshToken>>

    fun getAccessToken(): Flow<String?>

    suspend fun saveAccessToken(accessToken: String)

    suspend fun removeAccessToken()

    fun getRefreshToken(): Flow<String?>

    suspend fun saveRefreshToken(refreshToken: String)

    suspend fun removeRefreshToken()

    fun getIsSecondLaunch(): Flow<Boolean>

    suspend fun saveIsSecondLaunch(isSecondLaunch: Boolean)
}