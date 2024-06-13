package com.singa.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class GetStaticTranslationDetailResponse(

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("videoUrl")
    val videoUrl: String,

    @field:SerializedName("transcripts")
    val transcripts: List<StaticTranscriptsItem>,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("updatedAt")
    val updatedAt: String
)

data class StaticTranscriptsItem(

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("staticTranslationId")
    val staticTranslationId: Int,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("text")
    val text: String,

    @field:SerializedName("userId")
    val userId: Int,

    @field:SerializedName("timestamp")
    val timestamp: String,

    @field:SerializedName("updatedAt")
    val updatedAt: String
)
