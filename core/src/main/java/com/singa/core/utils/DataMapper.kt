package com.singa.core.utils

import com.singa.core.data.source.remote.response.GetMeResponse
import com.singa.core.data.source.remote.response.LoginResponse
import com.singa.core.data.source.remote.response.SchemaError
import com.singa.core.data.source.remote.response.UpdateTokenResponse
import com.singa.core.data.source.remote.response.UpdateUserResponse
import com.singa.core.domain.model.RefreshToken
import com.singa.core.domain.model.Token
import com.singa.core.domain.model.User
import com.singa.core.domain.model.ValidationErrorSchema


object DataMapper {
    fun mapResponseValidationErrorToModel(
        errors: List<SchemaError>
    ) = errors.map {
        ValidationErrorSchema(
            field = it.field,
            message = it.message,
            rule = it.rule
        )
    }

    fun mapLoginResponseToModel(
        data: LoginResponse
    ) = Token(
        type = data.type,
        accessToken = data.token,
        refreshToken = data.refreshToken
    )

    fun mapUserResponseToModel(
        data: GetMeResponse
    ) = User(
        id = data.id,
        name = data.name,
        email = data.email,
        avatar = data.avatar,
        isSignUser = data.isSignUser,
        createdAt = data.createdAt,
        updatedAt = data.updatedAt
    )

    fun mapRefreshTokenResponseToModel(
        data: UpdateTokenResponse
    ) = RefreshToken(
        type = data.type,
        token = data.token,
    )

    fun mapUpdateUserResponseToModel(
        data: UpdateUserResponse
    ) = User(
        id = data.id,
        name = data.name,
        email = data.email,
        avatar = data.avatarUrl,
        isSignUser = data.isSignUser,
        createdAt = data.createdAt,
        updatedAt = data.updatedAt
    )
}