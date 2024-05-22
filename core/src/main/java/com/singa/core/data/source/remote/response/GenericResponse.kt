package com.singa.core.data.source.remote.response
import com.google.gson.annotations.SerializedName

data class GenericResponse<T>(
    @field:SerializedName("meta")
    val meta: Meta,

    @field:SerializedName("data")
    val data: T,
)

data class Meta(
    @field:SerializedName("code")
    val code: Int,

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("message")
    val message: String,
)