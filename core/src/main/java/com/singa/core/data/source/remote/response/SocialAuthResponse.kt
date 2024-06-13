package com.singa.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class SocialAuthResponse(
    @field:SerializedName("meta")
    val meta: Meta,

    @field:SerializedName("data")
    val data: Data,
)

data class Data(

    @field:SerializedName("type")
    val type: String,

    @field:SerializedName("token")
    val token: String,

    @field:SerializedName("refreshToken")
    val refreshToken: String
)