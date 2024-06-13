package com.singa.core.domain.usecase

import com.singa.core.data.Resource
import com.singa.core.domain.model.StaticTranslation
import com.singa.core.domain.repository.ISingaRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

class SingaInteractor(
    private val singaRepository: ISingaRepository
) : SingaUseCase {
    override fun register(name: String, email: String, password: String) =
        singaRepository.register(name, email, password)

    override fun login(email: String, password: String) = singaRepository.login(email, password)

    override fun logout() = singaRepository.logout()

    override fun guest() = singaRepository.guest()

    override fun getMe() = singaRepository.getMe()

    override fun updateMe(
        name: String?,
        email: String?,
        password: String?,
        confirmPassword: String?,
        avatar: MultipartBody.Part?,
        isSignUser: Boolean?
    ) = singaRepository.updateMe(
        name,
        email,
        password,
        confirmPassword,
        avatar,
        isSignUser
    )

    override fun getConversations() = singaRepository.getConversations()

    override fun getVideoConversationDetails(translationId: Int, transcriptId: Int) =
        singaRepository.getVideoConversationDetails(translationId, transcriptId)

    override fun createConversation(title: String) = singaRepository.createConversation(title)

    override fun getConverstaionNodes(id: Int) = singaRepository.getConverstaionNodes(id)

    override fun getStaticTranslations() = singaRepository.getStaticTranslations()

    override fun getStaticTranslationDetail(staticTranslationId: Int) =
        singaRepository.getStaticTranslationDetail(staticTranslationId)

    override fun getArticles() = singaRepository.getArticles()

    override fun createNewSpeechConversation(
        text: String,
        conversationId: Int
    ) = singaRepository.createNewSpeechConversation(text, conversationId)

    override fun createNewVideoConversation(
        conversationId: Int,
        file: MultipartBody.Part
    ) = singaRepository.createNewVideoConversation(conversationId, file)

    override fun createNewStaticTranslation(
        title: String,
        file: MultipartBody.Part
    ): Flow<Resource<StaticTranslation>>  = singaRepository.createNewStaticTranslation(title, file)

    override fun deleteStaticTranslation(id: Int) = singaRepository.deleteStaticTranslation(id)

    override fun deleteConversationNode(id: Int) = singaRepository.deleteConversationNode(id)

    override fun bulkDeleteConversationNode(id: Set<Int>) = singaRepository.bulkDeleteConversationNode(id)

    override fun updateToken() = singaRepository.updateToken()

    override fun getAccessToken() = singaRepository.getAccessToken()

    override suspend fun saveAccessToken(accessToken: String) =
        singaRepository.saveAccessToken(accessToken)

    override suspend fun removeAccessToken() = singaRepository.removeAccessToken()

    override fun getRefreshToken() = singaRepository.getRefreshToken()

    override suspend fun saveRefreshToken(refreshToken: String) =
        singaRepository.saveRefreshToken(refreshToken)

    override suspend fun removeRefreshToken() = singaRepository.removeRefreshToken()

    override fun getIsSecondLaunch() = singaRepository.getIsSecondLaunch()

    override suspend fun saveIsSecondLaunch(isSecondLaunch: Boolean) =
        singaRepository.saveIsSecondLaunch(isSecondLaunch)

}