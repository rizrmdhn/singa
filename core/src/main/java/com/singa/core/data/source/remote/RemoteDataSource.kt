package com.singa.core.data.source.remote


import com.google.gson.Gson
import com.singa.core.data.source.remote.network.ApiResponse
import com.singa.core.data.source.remote.network.ApiService
import com.singa.core.data.source.remote.response.GenericResponse
import com.singa.core.data.source.remote.response.GenericSuccessResponse
import com.singa.core.data.source.remote.response.GetMeResponse
import com.singa.core.data.source.remote.response.LoginResponse
import com.singa.core.data.source.remote.response.SchemaErrorResponse
import com.singa.core.data.source.remote.response.UpdateTokenResponse
import com.singa.core.data.source.remote.response.UpdateUserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class RemoteDataSource(
    private val apiService: ApiService
) {
    fun register(body: RequestBody): Flow<ApiResponse<GenericSuccessResponse>> {
        return flow {
            try {
                val response = apiService.register(body)
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(ApiResponse.Success(it))
                    } ?: emit(ApiResponse.Empty)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, SchemaErrorResponse::class.java)
                    if (response.code() == 422) {
                        emit(ApiResponse.ValidationError(errorResponse.errors))
                    } else {
                        emit(ApiResponse.Error(errorResponse.message, response.code()))
                    }
                }
            } catch (e: Exception) {
                if (e is HttpException) {
                    val errorResponse = e.response()?.errorBody()?.string()
                    val errorJson = errorResponse?.let { JSONObject(it) }
                    val message = errorJson?.optString("message") ?: "Unknown error"
                    emit(ApiResponse.Error(message, e.code()))
                } else {
                    emit(ApiResponse.Error(e.message ?: "Unknown error", 0))
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun guest(): Flow<ApiResponse<GenericResponse<LoginResponse>>> {
        return flow {
            try {
                val response = apiService.guest()
                if (response.meta.status == "error") {
                    emit(ApiResponse.Error(response.meta.message, response.meta.code))
                } else {
                    emit(ApiResponse.Success(response))
                }
            } catch (e: Exception) {
                if (e is HttpException) {
                    val exception: HttpException = e
                    val response = exception.response()
                    try {
                        val jsonObject = JSONObject(response?.errorBody()?.string() ?: "Error")
                        emit(ApiResponse.Error(jsonObject.optString("message"), response?.code() ?: 0))
                    } catch (e1: JSONException) {
                        e1.printStackTrace()
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }
                } else {
                    emit(ApiResponse.Error(e.toString(), 0))
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun login(body: RequestBody): Flow<ApiResponse<GenericResponse<LoginResponse>>> {
        return flow {
            try {
                val response = apiService.login(body)
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(ApiResponse.Success(it))
                    } ?: emit(ApiResponse.Empty)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, SchemaErrorResponse::class.java)
                    if (response.code() == 422) {
                        emit(ApiResponse.ValidationError(errorResponse.errors))
                    } else {
                        emit(ApiResponse.Error(errorResponse.message, response.code()))
                    }
                }
            } catch (e: Exception) {
                if (e is HttpException) {
                    val errorResponse = e.response()?.errorBody()?.string()
                    val errorJson = errorResponse?.let { JSONObject(it) }
                    val message = errorJson?.optString("message") ?: "Unknown error"
                    emit(ApiResponse.Error(message, e.code()))
                } else {
                    emit(ApiResponse.Error(e.message ?: "Unknown error", 0))
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getMe(): Flow<ApiResponse<GenericResponse<GetMeResponse>>> {
        return flow {
            try {
                val response = apiService.getMe()
                if (response.meta.status == "error") {
                    emit(ApiResponse.Error(response.meta.message, response.meta.code))
                } else {
                    emit(ApiResponse.Success(response))
                }
            } catch (e: Exception) {
                if (e is HttpException) {
                    val exception: HttpException = e
                    val response = exception.response()
                    try {
                        val jsonObject = JSONObject(response?.errorBody()?.string() ?: "Error")
                        emit(ApiResponse.Error(jsonObject.optString("message"), response?.code() ?: 0))
                    } catch (e1: JSONException) {
                        e1.printStackTrace()
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }
                } else {
                    emit(ApiResponse.Error(e.toString(), 0))
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun logout(): Flow<ApiResponse<GenericSuccessResponse>> {
        return flow {
            try {
                val response = apiService.logout()
                if (response.meta.status == "error") {
                    emit(ApiResponse.Error(response.meta.message, response.meta.code))
                } else {
                    emit(ApiResponse.Success(response))
                }
            } catch (e: Exception) {
                if (e is HttpException) {
                    val exception: HttpException = e
                    val response = exception.response()
                    try {
                        val jsonObject = JSONObject(response?.errorBody()?.string() ?: "Error")
                        emit(ApiResponse.Error(jsonObject.optString("message"), response?.code() ?: 0))
                    } catch (e1: JSONException) {
                        e1.printStackTrace()
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }
                } else {
                    emit(ApiResponse.Error(e.toString(), 0))
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun updateToken(body: RequestBody): Flow<ApiResponse<GenericResponse<UpdateTokenResponse>>> {
        return flow {
            try {
                val response = apiService.updateToken(body)
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(ApiResponse.Success(it))
                    } ?: emit(ApiResponse.Empty)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, SchemaErrorResponse::class.java)
                    if (response.code() == 422) {
                        emit(ApiResponse.ValidationError(errorResponse.errors))
                    } else {
                        emit(ApiResponse.Error(errorResponse.message, response.code()))
                    }
                }
            } catch (e: Exception) {
                if (e is HttpException) {
                    val exception: HttpException = e
                    val response = exception.response()
                    try {
                        val jsonObject = JSONObject(response?.errorBody()?.string() ?: "Error")
                        emit(ApiResponse.Error(jsonObject.optString("message"), response?.code() ?: 0))
                    } catch (e1: JSONException) {
                        e1.printStackTrace()
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }
                } else {
                    emit(ApiResponse.Error(e.toString(), 0))
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun updateMe(body: RequestBody): Flow<ApiResponse<GenericResponse<UpdateUserResponse>>> {
        return flow {
            try {
                val response = apiService.updateMe(body)
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(ApiResponse.Success(it))
                    } ?: emit(ApiResponse.Empty)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, SchemaErrorResponse::class.java)
                    if (response.code() == 422) {
                        emit(ApiResponse.ValidationError(errorResponse.errors))
                    } else {
                        emit(ApiResponse.Error(errorResponse.message, response.code()))
                    }
                }
            } catch (e: Exception) {
                if (e is HttpException) {
                    val exception: HttpException = e
                    val response = exception.response()
                    try {
                        val jsonObject = JSONObject(response?.errorBody()?.string() ?: "Error")
                        emit(ApiResponse.Error(jsonObject.optString("message"), response?.code() ?: 0))
                    } catch (e1: JSONException) {
                        e1.printStackTrace()
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }
                } else {
                    emit(ApiResponse.Error(e.toString(), 0))
                }
            }
        }.flowOn(Dispatchers.IO)
    }
}