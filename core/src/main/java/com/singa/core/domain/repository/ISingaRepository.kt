package com.singa.core.domain.repository

import com.singa.core.data.Resource
import com.singa.core.domain.model.Conversation
import com.singa.core.domain.model.RefreshToken
import com.singa.core.domain.model.StaticTranslation
import com.singa.core.domain.model.Token
import com.singa.core.domain.model.User
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ISingaRepository {
    fun register(name: String, email: String, password: String): Flow<Resource<String>>

    fun login(email: String, password: String): Flow<Resource<Token>>

    fun logout(): Flow<Resource<String>>

    fun guest(): Flow<Resource<Token>>

    fun getMe(): Flow<Resource<User>>

    fun updateMe(
        name: String?,
        email: String?,
        password: String?,
        confirmPassword: String?,
        avatar: File?,
        isSignUser: Boolean?
    ): Flow<Resource<User>>

    fun getConversations(): Flow<Resource<List<Conversation>>>

    fun getStaticTranslations(): Flow<Resource<List<StaticTranslation>>>

    fun updateToken(): Flow<Resource<RefreshToken>>

    fun getAccessToken(): Flow<String>

    suspend fun saveAccessToken(token: String)

    suspend fun removeAccessToken()

    fun getRefreshToken(): Flow<String>

    suspend fun saveRefreshToken(token: String)

    suspend fun removeRefreshToken()

    fun getIsSecondLaunch(): Flow<Boolean>

    suspend fun saveIsSecondLaunch(isFirstLaunch: Boolean)
}