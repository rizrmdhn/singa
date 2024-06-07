package com.singa.core.data.source.remote.network

import com.singa.core.data.source.remote.response.ArticlesItem
import com.singa.core.data.source.remote.response.CreateNewSpeechConversation
import com.singa.core.data.source.remote.response.GenericResponse
import com.singa.core.data.source.remote.response.GenericSuccessResponse
import com.singa.core.data.source.remote.response.GetConversationListItem
import com.singa.core.data.source.remote.response.GetConversationNode
import com.singa.core.data.source.remote.response.GetMeResponse
import com.singa.core.data.source.remote.response.GetStaticTranslationDetailResponse
import com.singa.core.data.source.remote.response.GetStaticTranslationList
import com.singa.core.data.source.remote.response.LoginResponse
import com.singa.core.data.source.remote.response.UpdateTokenResponse
import com.singa.core.data.source.remote.response.UpdateUserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path


interface ApiService {
    // Singa API
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
    ): Response<GenericSuccessResponse>

    @Headers("Content-Type: application/json")
    @POST("refresh")
    suspend fun updateToken(
        @Body body: RequestBody
    ): Response<GenericResponse<UpdateTokenResponse>>

    @Multipart
    @PUT("users/me")
    suspend fun updateMe(
        @Part avatar: MultipartBody.Part?,
        @Part("name") name: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("password") password: RequestBody?,
        @Part("password_confirmation") confirmPassword: RequestBody?,
        @Part("isSignUser") isSignUser: RequestBody?,
    ): Response<GenericResponse<UpdateUserResponse>>

    @Headers("Content-Type: application/json")
    @GET("translation/conversation")
    suspend fun getConversations(): GenericResponse<List<GetConversationListItem>>

    @Headers("Content-Type: application/json")
    @GET("translation/static")
    suspend fun getStaticTranslations(): GenericResponse<List<GetStaticTranslationList>>

    @Headers("Content-Type: application/json")
    @GET("translation/static/{id}")
    suspend fun getStaticDetailTranslation(@Path("id") id: Int): GenericResponse<GetStaticTranslationDetailResponse>

    @Headers("Content-Type: application/json")
    @GET("translation/conversation/{id}")
    suspend fun getConversationNodes(@Path("id") id: Int): GenericResponse<List<GetConversationNode>>

    @Headers("Content-Type: application/json")
    @POST("translation/conversation")
    suspend fun createConversationNode(
        @Body body: RequestBody
    ): Response<GenericResponse<GetConversationListItem>>

    @Headers("Content-Type: application/json")
    @GET("articles")
    suspend fun getArticles(): GenericResponse<List<ArticlesItem>>

    @Headers("Content-Type: application/json")
    @POST("translation/conversation/{id}/speech")
    suspend fun createNewSpeechConversation(
        @Path("id") id: Int,
        @Body body: RequestBody
    ): Response<GenericResponse<CreateNewSpeechConversation>>

    @Headers("Content-Type: application/json")
    @DELETE("translation/conversation/{id}")
    suspend fun deleteConversationNode(@Path("id") id: Int): GenericSuccessResponse
}

