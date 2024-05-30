package com.singa.core.data.source.remote.response

data class TestLoginResponse(
    val meta: Meta,
    val data: Data
)

data class Data(
    val type: String,
    val token: String,
    val refreshToken: String
)