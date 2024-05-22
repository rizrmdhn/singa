package com.singa.asl.common

import com.singa.core.common.UiText

data class ValidationState(
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
)