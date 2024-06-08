package com.singa.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConversationNode(
    val createdAt: String,
    val conversationTranslationId: Int,
    val videoUrl: String? = null,
    val transcripts: String,
    val id: Int,
    val video: String? = null,
    val type: String,
    val status: String,
    val userId: Int,
    val updatedAt: String,
    var isSelected: Boolean = false
): Parcelable