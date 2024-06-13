package com.singa.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoConversation(
    val createdAt: String,
    val transcript: Transcript? = null,
    val conversationTranslationId: Int,
    val id: Int,
    val type: String,
    val userId: Int,
    val updatedAt: String,
    val video: String,
    val videoUrl: String
): Parcelable