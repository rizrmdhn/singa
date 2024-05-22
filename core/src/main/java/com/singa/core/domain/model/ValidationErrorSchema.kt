package com.singa.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ValidationErrorSchema(
    val message: String,
    val rule: String,
    val field: String
) : Parcelable