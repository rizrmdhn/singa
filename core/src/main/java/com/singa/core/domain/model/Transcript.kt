package com.singa.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transcript(
    val createdAt: String,
    val text: String,
    val id: Int,
    val userId: Int,
    val conversationNodeId: Int,
    val timestamp: String,
    val updatedAt: String
): Parcelable