package com.singa.core.data.source.remote.network

import com.singa.core.data.source.remote.response.SchemaError

sealed class ApiResponse<out R> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val errorMessage: String,val errorCode: Int) : ApiResponse<Nothing>()
    data class ValidationError(val errors: List<SchemaError>) : ApiResponse<Nothing>()
    data object Empty : ApiResponse<Nothing>()
}