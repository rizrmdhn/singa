package com.singa.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Token(
    val type: String,
    val accessToken: String,
    val refreshToken: String
): Parcelable