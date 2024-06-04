package com.singa.core.domain.usecase

import com.singa.core.data.Resource
import com.singa.core.domain.model.Articles
import com.singa.core.domain.model.Conversation
import com.singa.core.domain.model.ConversationNode
import com.singa.core.domain.model.RefreshToken
import com.singa.core.domain.model.StaticTranslation
import com.singa.core.domain.model.StaticTranslationDetail
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
        email: String?,
        password: String?,
        confirmPassword: String?,
        avatar: File?,
        isSignUser: Boolean?
    ): Flow<Resource<User>>

    fun getConversations(): Flow<Resource<List<Conversation>>>

    fun createConversation(title: String): Flow<Resource<Conversation>>

    fun getConverstaionNodes(id: Int): Flow<Resource<List<ConversationNode>>>

    fun getStaticTranslations(): Flow<Resource<List<StaticTranslation>>>

    fun getStaticTranslationDetail(staticTranslationId: Int): Flow<Resource<StaticTranslationDetail>>

    fun getArticles(): Flow<Resource<List<Articles>>>


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