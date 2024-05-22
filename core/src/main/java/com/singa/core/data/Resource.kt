package com.singa.core.data

import com.singa.core.domain.model.ValidationErrorSchema


sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
    data class ValidationError(val errors: List<ValidationErrorSchema>) : Resource<Nothing>()
    data class Loading(val data: Any? = null) : Resource<Nothing>()
}
