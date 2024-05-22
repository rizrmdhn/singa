package com.singa.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int,
    val name: String,
    val email: String? = null,
    val avatar: String? = null,
    val isSignUser: Boolean,
    val createdAt: String,
    val updatedAt: String
) : Parcelable