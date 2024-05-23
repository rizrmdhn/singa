package com.singa.core.data.source.remote.network

import com.singa.core.data.source.remote.response.GenericResponse
import com.singa.core.data.source.remote.response.GenericSuccessResponse
import com.singa.core.data.source.remote.response.GetMeResponse
import com.singa.core.data.source.remote.response.LoginResponse
import com.singa.core.data.source.remote.response.UpdateTokenResponse
import com.singa.core.data.source.remote.response.UpdateUserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part


interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("register")
    suspend fun register(
        @Body body: RequestBody
    ): Response<GenericSuccessResponse>

    @Headers("Content-Type: application/json")
    @POST("guest")
    suspend fun guest(): GenericResponse<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("login")
    suspend fun login(
        @Body body: RequestBody
    ): Response<GenericResponse<LoginResponse>>

    @Headers("Content-Type: application/json")
    @GET("users/me")
    suspend fun getMe(): GenericResponse<GetMeResponse>

    @Headers("Content-Type: application/json")
    @POST("logout")
    suspend fun logout(
        @Body body: RequestBody
    ): GenericSuccessResponse

    @Headers("Content-Type: application/json")
    @POST("update-token")
    suspend fun updateToken(
        @Body body: RequestBody
    ): Response<GenericResponse<UpdateTokenResponse>>

    @Headers("Content-Type: application/json")
    @Multipart
    @PUT("users/me")
    suspend fun updateMe(
        @Part avatar: MultipartBody.Part?,
        @Part("name") name: RequestBody?,
        @Part("password") password: RequestBody?,
        @Part("isSignUser") isSignUser: RequestBody?,
    ): Response<GenericResponse<UpdateUserResponse>>
}

