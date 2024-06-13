package com.singa.core.data

import com.google.gson.JsonObject
import com.singa.core.data.source.local.LocalDataSource
import com.singa.core.data.source.remote.RemoteDataSource
import com.singa.core.data.source.remote.network.ApiResponse
import com.singa.core.domain.model.Articles
import com.singa.core.domain.model.Conversation
import com.singa.core.domain.model.ConversationNode
import com.singa.core.domain.model.DetailVideoConversation
import com.singa.core.domain.model.RefreshToken
import com.singa.core.domain.model.SpeechConversation
import com.singa.core.domain.model.StaticTranslation
import com.singa.core.domain.model.StaticTranslationDetail
import com.singa.core.domain.model.Token
import com.singa.core.domain.model.User
import com.singa.core.domain.model.VideoConversation
import com.singa.core.domain.repository.ISingaRepository
import com.singa.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class SingaRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : ISingaRepository {

    override fun register(name: String, email: String, password: String): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading())
            val body = JsonObject().apply {
                addProperty("name", name)
                addProperty("email", email)
                addProperty("password", password)
            }.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            remoteDataSource.register(body).collect {
                when (it) {
                    is ApiResponse.Success -> {
                        emit(Resource.Success(it.data.meta.message))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Error("Empty Data"))
                    }

                    is ApiResponse.Error -> {
                        emit(Resource.Error(it.errorMessage))
                    }

                    is ApiResponse.ValidationError -> {
                        val validationErrors = it.errors
                        val parcelableErrors =
                            DataMapper.mapResponseValidationErrorToModel(validationErrors)
                        emit(Resource.ValidationError(parcelableErrors))
                    }
                }
            }
        }
    }

    override fun guest(): Flow<Resource<Token>> {
        return flow {
            emit(Resource.Loading())
            remoteDataSource.guest().collect {
                when (it) {
                    is ApiResponse.Success -> {
                        val token = DataMapper.mapLoginResponseToModel(it.data.data)
                        emit(Resource.Success(token))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Error("Empty Data"))
                    }

                    is ApiResponse.Error -> {
                        emit(Resource.Error(it.errorMessage))
                    }

                    is ApiResponse.ValidationError -> {
                        val validationErrors = it.errors
                        val parcelableErrors =
                            DataMapper.mapResponseValidationErrorToModel(validationErrors)
                        emit(Resource.ValidationError(parcelableErrors))
                    }
                }
            }

        }
    }

    override fun login(email: String, password: String): Flow<Resource<Token>> {
        return flow {
            emit(Resource.Loading())
            val body = JsonObject().apply {
                addProperty("email", email)
                addProperty("password", password)
            }.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            remoteDataSource.login(body).collect {
                when (it) {
                    is ApiResponse.Success -> {
                        val token = DataMapper.mapLoginResponseToModel(it.data.data)
                        emit(Resource.Success(token))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Error("Empty Data"))
                    }

                    is ApiResponse.Error -> {
                        emit(Resource.Error(it.errorMessage))
                    }

                    is ApiResponse.ValidationError -> {
                        val validationErrors = it.errors
                        val parcelableErrors =
                            DataMapper.mapResponseValidationErrorToModel(validationErrors)
                        emit(Resource.ValidationError(parcelableErrors))
                    }
                }
            }
        }
    }

    override fun getMe(): Flow<Resource<User>> {
        return flow {
            emit(Resource.Loading())
            remoteDataSource.getMe().collect { it ->
                when (it) {
                    is ApiResponse.Success -> {
                        val user = DataMapper.mapUserResponseToModel(it.data.data)
                        emit(Resource.Success(user))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Error("Empty Data"))
                    }

                    is ApiResponse.Error -> {
                        // if error code is 401, then get refresh token to update token
                        if (it.errorCode == 401) {
                            updateToken().collect {
                                when (it) {
                                    is Resource.Success -> {
                                        saveAccessToken(it.data.token)
                                        getMe().collect { newUser ->
                                            emit(newUser)
                                        }
                                    }

                                    is Resource.Empty -> {
                                        emit(Resource.Error("Empty Data"))
                                    }

                                    is Resource.Error -> {
                                        emit(Resource.Error(it.message))
                                    }

                                    is Resource.ValidationError -> {
                                        emit(Resource.ValidationError(it.errors))
                                    }

                                    is Resource.Loading -> {
                                        emit(Resource.Loading())
                                    }
                                }
                            }
                        } else {
                            emit(Resource.Error(it.errorMessage))
                        }
                    }

                    is ApiResponse.ValidationError -> {
                        val validationErrors = it.errors
                        val parcelableErrors =
                            DataMapper.mapResponseValidationErrorToModel(validationErrors)
                        emit(Resource.ValidationError(parcelableErrors))
                    }
                }
            }
        }
    }

    override fun logout(): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading())
            val token = getRefreshToken().first()

            if (token.isEmpty()) {
                emit(Resource.Error("Token is empty"))
                return@flow
            }

            val body = JsonObject().apply {
                addProperty("refreshToken", token)
            }.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            remoteDataSource.logout(body).collect {
                when (it) {
                    is ApiResponse.Success -> {
                        emit(Resource.Success(it.data.meta.message))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Error("Empty Data"))
                    }

                    is ApiResponse.Error -> {
                        emit(Resource.Error(it.errorMessage))
                    }

                    is ApiResponse.ValidationError -> {
                        val validationErrors = it.errors
                        val parcelableErrors =
                            DataMapper.mapResponseValidationErrorToModel(validationErrors)
                        emit(Resource.ValidationError(parcelableErrors))
                    }
                }
            }
        }
    }

    override fun updateToken(): Flow<Resource<RefreshToken>> {
        return flow {
            emit(Resource.Loading())
            val token = getRefreshToken().first()
            val body = JsonObject().apply {
                addProperty("token", token)
            }.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            remoteDataSource.updateToken(body).collect {
                when (it) {
                    is ApiResponse.Success -> {
                        val tokenResponse = DataMapper.mapRefreshTokenResponseToModel(it.data.data)
                        emit(Resource.Success(tokenResponse))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Error("Empty Data"))
                    }

                    is ApiResponse.Error -> {
                        emit(Resource.Error(it.errorMessage))
                    }

                    is ApiResponse.ValidationError -> {
                        val validationErrors = it.errors
                        val parcelableErrors =
                            DataMapper.mapResponseValidationErrorToModel(validationErrors)
                        emit(Resource.ValidationError(parcelableErrors))
                    }
                }
            }
        }
    }

    override fun updateMe(
        name: String?,
        email: String?,
        password: String?,
        confirmPassword: String?,
        avatar: MultipartBody.Part?,
        isSignUser: Boolean?
    ): Flow<Resource<User>> {
        return flow {
            emit(Resource.Loading())
            val requestName = name?.toRequestBody("text/plain".toMediaTypeOrNull())
            val requestEmail = email?.toRequestBody("text/plain".toMediaTypeOrNull())
            val requestPassword = password?.toRequestBody("text/plain".toMediaTypeOrNull())
            val requestConfirmPassword =
                confirmPassword?.toRequestBody("text/plain".toMediaTypeOrNull())
            val requestIsSignUser =
                isSignUser.toString().toRequestBody("text/plain".toMediaTypeOrNull())


            remoteDataSource.updateMe(
                requestName,
                requestEmail,
                requestPassword,
                requestConfirmPassword,
                requestIsSignUser,
                avatar
            ).collect {
                when (it) {
                    is ApiResponse.Success -> {
                        val user = DataMapper.mapUpdateUserResponseToModel(it.data.data)
                        emit(Resource.Success(user))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Error("Empty Data"))
                    }

                    is ApiResponse.Error -> {
                        emit(Resource.Error(it.errorMessage))
                    }

                    is ApiResponse.ValidationError -> {
                        val validationErrors = it.errors
                        val parcelableErrors =
                            DataMapper.mapResponseValidationErrorToModel(validationErrors)
                        emit(Resource.ValidationError(parcelableErrors))
                    }
                }
            }
        }
    }

    override fun getConversations(): Flow<Resource<List<Conversation>>> {
        return flow {
            emit(Resource.Loading())
            remoteDataSource.getConversations().collect {
                when (it) {
                    is ApiResponse.Success -> {
                        val conversations = DataMapper.mapConversationResponseToModel(it.data.data)
                        emit(Resource.Success(conversations))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Empty())
                    }

                    is ApiResponse.Error -> {
                        emit(Resource.Error(it.errorMessage))
                    }

                    is ApiResponse.ValidationError -> {
                        val validationErrors = it.errors
                        val parcelableErrors =
                            DataMapper.mapResponseValidationErrorToModel(validationErrors)
                        emit(Resource.ValidationError(parcelableErrors))
                    }
                }
            }
        }
    }

    override fun getVideoConversationDetails(
        translationId: Int,
        transcriptId: Int
    ): Flow<Resource<DetailVideoConversation>> {
        return flow {
            emit(Resource.Loading())
            remoteDataSource.getVideoConversationDetails(translationId, transcriptId).collect {
                when (it) {
                    is ApiResponse.Success -> {
                        val videoConversation =
                            DataMapper.mapVideoConversationDetailResponseToModel(it.data.data)
                        emit(Resource.Success(videoConversation))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Error("Empty Data"))
                    }

                    is ApiResponse.Error -> {
                        emit(Resource.Error(it.errorMessage))
                    }

                    is ApiResponse.ValidationError -> {
                        val validationErrors = it.errors
                        val parcelableErrors =
                            DataMapper.mapResponseValidationErrorToModel(validationErrors)
                        emit(Resource.ValidationError(parcelableErrors))
                    }
                }
            }
        }
    }

    override fun createConversation(title: String): Flow<Resource<Conversation>> {
        return flow {
            emit(Resource.Loading())
            val body = JsonObject().apply {
                addProperty("title", title)
            }.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            remoteDataSource.createConversation(body).collect {
                when (it) {
                    is ApiResponse.Success -> {
                        val conversation =
                            DataMapper.mapConversationCreateResponseToModel(it.data.data)
                        emit(Resource.Success(conversation))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Error("Empty Data"))
                    }

                    is ApiResponse.Error -> {
                        emit(Resource.Error(it.errorMessage))
                    }

                    is ApiResponse.ValidationError -> {
                        val validationErrors = it.errors
                        val parcelableErrors =
                            DataMapper.mapResponseValidationErrorToModel(validationErrors)
                        emit(Resource.ValidationError(parcelableErrors))
                    }
                }
            }
        }
    }

    override fun getConverstaionNodes(
        id: Int,
    ): Flow<Resource<List<ConversationNode>>> {
        return flow {
            emit(Resource.Loading())
            remoteDataSource.getConversationNodes(id).collect {
                when (it) {
                    is ApiResponse.Success -> {
                        val conversationNodes =
                            DataMapper.mapConversationNodeResponseToModel(it.data.data)
                        emit(Resource.Success(conversationNodes))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Empty())
                    }

                    is ApiResponse.Error -> {
                        emit(Resource.Error(it.errorMessage))
                    }

                    is ApiResponse.ValidationError -> {
                        val validationErrors = it.errors
                        val parcelableErrors =
                            DataMapper.mapResponseValidationErrorToModel(validationErrors)
                        emit(Resource.ValidationError(parcelableErrors))
                    }
                }
            }
        }
    }

    override fun getStaticTranslations(): Flow<Resource<List<StaticTranslation>>> {
        return flow {
            emit(Resource.Loading())
            remoteDataSource.getStaticTranslations().collect {
                when (it) {
                    is ApiResponse.Success -> {
                        val translations =
                            DataMapper.mapStaticTranslationResponseToModel(it.data.data)
                        emit(Resource.Success(translations))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Empty())
                    }

                    is ApiResponse.Error -> {
                        emit(Resource.Error(it.errorMessage))
                    }

                    is ApiResponse.ValidationError -> {
                        val validationErrors = it.errors
                        val parcelableErrors =
                            DataMapper.mapResponseValidationErrorToModel(validationErrors)
                        emit(Resource.ValidationError(parcelableErrors))
                    }
                }
            }
        }
    }

    override fun getStaticTranslationDetail(staticTranslationId: Int): Flow<Resource<StaticTranslationDetail>> {
        return flow {
            emit(Resource.Loading())
            remoteDataSource.getStaticTranslationsDetail(staticTranslationId).collect {
                when (it) {
                    is ApiResponse.Success -> {
                        val translationDetail =
                            DataMapper.mapStaticTranslationDetailResponseToModel(it.data.data)
                        emit(Resource.Success(translationDetail))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Error("Empty Data"))
                    }

                    is ApiResponse.Error -> {
                        emit(Resource.Error(it.errorMessage))
                    }

                    is ApiResponse.ValidationError -> {
                        val validationErrors = it.errors
                        val parcelableErrors =
                            DataMapper.mapResponseValidationErrorToModel(validationErrors)
                        emit(Resource.ValidationError(parcelableErrors))
                    }
                }
            }
        }
    }

    override fun createNewStaticTranslation(
        title: String,
        file: MultipartBody.Part
    ): Flow<Resource<StaticTranslation>> {
        return flow {
            emit(Resource.Loading())
            val requestTitle = title.toRequestBody("text/plain".toMediaTypeOrNull())
            remoteDataSource.createStaticTranslation(requestTitle, file).collect {
                when (it) {
                    is ApiResponse.Success -> {
                        val staticTranslation =
                            DataMapper.mapCreateStaticTranslationToModel(it.data.data)
                        emit(Resource.Success(staticTranslation))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Error("Empty Data"))
                    }

                    is ApiResponse.Error -> {
                        emit(Resource.Error(it.errorMessage))
                    }

                    is ApiResponse.ValidationError -> {
                        val validationErrors = it.errors
                        val parcelableErrors =
                            DataMapper.mapResponseValidationErrorToModel(validationErrors)
                        emit(Resource.ValidationError(parcelableErrors))
                    }
                }
            }
        }
    }

    override fun deleteStaticTranslation(id: Int): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading())
            remoteDataSource.deleteStaticTranslation(id).collect {
                when (it) {
                    is ApiResponse.Success -> {
                        emit(Resource.Success(it.data.meta.message))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Error("Empty Data"))
                    }

                    is ApiResponse.Error -> {
                        emit(Resource.Error(it.errorMessage))
                    }

                    is ApiResponse.ValidationError -> {
                        val validationErrors = it.errors
                        val parcelableErrors =
                            DataMapper.mapResponseValidationErrorToModel(validationErrors)
                        emit(Resource.ValidationError(parcelableErrors))
                    }
                }
            }
        }
    }

    override fun getArticles(): Flow<Resource<List<Articles>>> {
        return flow {
            emit(Resource.Loading())
            remoteDataSource.getArticles().collect {
                when (it) {
                    is ApiResponse.Success -> {
                        val articles = DataMapper.mapArticlesResponseToModel(it.data.data)
                        emit(Resource.Success(articles))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Empty())
                    }

                    is ApiResponse.Error -> {
                        emit(Resource.Error(it.errorMessage))
                    }

                    is ApiResponse.ValidationError -> {
                        val validationErrors = it.errors
                        val parcelableErrors =
                            DataMapper.mapResponseValidationErrorToModel(validationErrors)
                        emit(Resource.ValidationError(parcelableErrors))
                    }
                }
            }
        }
    }

    override fun createNewSpeechConversation(
        text: String,
        conversationId: Int
    ): Flow<Resource<SpeechConversation>> {
        return flow {
            emit(Resource.Loading())
            val body = JsonObject().apply {
                addProperty("text", text)
                addProperty("type", "speech")
            }.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            remoteDataSource.createNewSpeechConversation(conversationId, body).collect {
                when (it) {
                    is ApiResponse.Success -> {
                        val speechConversation =
                            DataMapper.mapSpeechConversationResponseToModel(it.data.data)
                        emit(Resource.Success(speechConversation))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Error("Empty Data"))
                    }

                    is ApiResponse.Error -> {
                        emit(Resource.Error(it.errorMessage))
                    }

                    is ApiResponse.ValidationError -> {
                        val validationErrors = it.errors
                        val parcelableErrors =
                            DataMapper.mapResponseValidationErrorToModel(validationErrors)
                        emit(Resource.ValidationError(parcelableErrors))
                    }
                }
            }
        }
    }

    override fun createNewVideoConversation(
        conversationId: Int,
        file: MultipartBody.Part
    ): Flow<Resource<VideoConversation>> {
        return flow {
            val body = "video".toRequestBody("text/plain".toMediaTypeOrNull())
            emit(Resource.Loading())
            remoteDataSource.createNewVideoConversation(conversationId, file, body).collect {
                when (it) {
                    is ApiResponse.Success -> {
                        val videoConversation =
                            DataMapper.mapVideoConversationResponseToModel(it.data.data)
                        emit(Resource.Success(videoConversation))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Error("Empty Data"))
                    }

                    is ApiResponse.Error -> {
                        emit(Resource.Error(it.errorMessage))
                    }

                    is ApiResponse.ValidationError -> {
                        val validationErrors = it.errors
                        val parcelableErrors =
                            DataMapper.mapResponseValidationErrorToModel(validationErrors)
                        emit(Resource.ValidationError(parcelableErrors))
                    }
                }
            }
        }
    }

    override fun bulkDeleteConversationNode(id: Set<Int>): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading())
            val convertedId = id.toList()

            remoteDataSource.bulkDeleteConversationNode(convertedId).collect {
                when (it) {
                    is ApiResponse.Success -> {
                        emit(Resource.Success(it.data.meta.message))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Error("Empty Data"))
                    }

                    is ApiResponse.Error -> {
                        emit(Resource.Error(it.errorMessage))
                    }

                    is ApiResponse.ValidationError -> {
                        val validationErrors = it.errors
                        val parcelableErrors =
                            DataMapper.mapResponseValidationErrorToModel(validationErrors)
                        emit(Resource.ValidationError(parcelableErrors))
                    }
                }
            }
        }
    }

    override fun deleteConversationNode(id: Int): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading())
            remoteDataSource.deleteConversationNode(id).collect {
                when (it) {
                    is ApiResponse.Success -> {
                        emit(Resource.Success(it.data.meta.message))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Error("Empty Data"))
                    }

                    is ApiResponse.Error -> {
                        emit(Resource.Error(it.errorMessage))
                    }

                    is ApiResponse.ValidationError -> {
                        val validationErrors = it.errors
                        val parcelableErrors =
                            DataMapper.mapResponseValidationErrorToModel(validationErrors)
                        emit(Resource.ValidationError(parcelableErrors))
                    }
                }
            }
        }
    }

    override fun getAccessToken(): Flow<String> {
        return localDataSource.getAccessToken()
    }

    override suspend fun saveAccessToken(token: String) {
        return localDataSource.saveAccessToken(token)
    }

    override suspend fun removeAccessToken() {
        localDataSource.removeAccessToken()
    }

    override fun getRefreshToken(): Flow<String> {
        return localDataSource.getRefreshToken()
    }

    override suspend fun removeRefreshToken() {
        localDataSource.removeRefreshToken()
    }

    override suspend fun saveRefreshToken(token: String) {
        return localDataSource.saveRefreshToken(token)
    }

    override fun getIsSecondLaunch(): Flow<Boolean> {
        return localDataSource.getIsSecondLaunch()
    }

    override suspend fun saveIsSecondLaunch(isFirstLaunch: Boolean) {
        localDataSource.saveIsSecondLaunch(isFirstLaunch)
    }
}