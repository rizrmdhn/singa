package com.singa.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SpeechConversation(
    val createdAt: String,
    val transcript: Transcript,
    val conversationTranslationId: Int,
    val id: Int,
    val type: String,
    val userId: Int,
    val updatedAt: String
): Parcelable