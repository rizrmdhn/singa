package com.singa.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StaticTranslation(
    val createdAt: String,
    val videoUrl: String,
    val id: Int,
    val title: String,
    val updatedAt: String
) : Parcelable

@Parcelize
data class StaticTranslationDetail(
    val createdAt: String,

    val videoUrl: String,

    val transcripts: List<StaticTranscriptsItem>,

    val id: Int,

    val title: String,

    val updatedAt: String
) : Parcelable

@Parcelize
data class StaticTranscriptsItem(

    val createdAt: String,

    val staticTranslationId: Int,

    val id: Int,

    val text: String,

    val userId: Int,

    val timestamp: String,

    val updatedAt: String
) : Parcelable