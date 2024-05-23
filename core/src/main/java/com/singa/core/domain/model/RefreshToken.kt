package com.singa.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RefreshToken(
    val type: String,
    val token: String
): Parcelable