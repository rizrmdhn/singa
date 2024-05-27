package com.singa.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Conversation(
    val createdAt: String,
    val id: Int,
    val title: String,
    val updatedAt: String
): Parcelable