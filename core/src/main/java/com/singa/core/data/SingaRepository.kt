package com.singa.core.data

import com.singa.core.data.source.local.LocalDataSource
import com.singa.core.data.source.remote.RemoteDataSource
import com.singa.core.data.source.remote.network.ApiResponse
import com.singa.core.domain.model.RefreshToken
import com.singa.core.domain.model.Token
import com.singa.core.domain.model.User
import com.singa.core.domain.repository.ISingaRepository
import com.singa.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SingaRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : ISingaRepository {

    override fun register(email: String, password: String): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading())
            val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email", email)
                .addFormDataPart("password", password)
                .build()
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
                        val parcelableErrors = DataMapper.mapResponseValidationErrorToModel(validationErrors)
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
            val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email", email)
                .addFormDataPart("password", password)
                .build()
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
                            var refreshToken: String? = null
                            getRefreshToken().collect {token ->
                                refreshToken = token
                            }
                            refreshToken?.let { it1 ->
                                updateToken(it1).collect {
                                    when (it) {
                                        is Resource.Success -> {
                                            saveAccessToken(it.data.token)
                                            getMe().collect {newUser ->
                                                emit(newUser)
                                            }
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
            remoteDataSource.logout().collect {
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

    override fun updateToken(refreshToken: String): Flow<Resource<RefreshToken>> {
        return flow {
            emit(Resource.Loading())
            val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("refresh_token", refreshToken)
                .build()
            remoteDataSource.updateToken(body).collect {
                when (it) {
                    is ApiResponse.Success -> {
                        val token = DataMapper.mapRefreshTokenResponseToModel(it.data.data)
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

    override fun updateMe(name: String, email: String, avatar: String): Flow<Resource<User>> {
        return flow {
            emit(Resource.Loading())
            val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", name)
                .addFormDataPart("email", email)
                .addFormDataPart("avatar", avatar)
                .build()
            remoteDataSource.updateMe(body).collect {
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