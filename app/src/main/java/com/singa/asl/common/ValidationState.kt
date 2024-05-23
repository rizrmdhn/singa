package com.singa.asl.common


data class ValidationState(
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val avatarError: String? = null
)