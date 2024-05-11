package com.singa.core.domain.usecase

import kotlinx.coroutines.flow.Flow

interface SingaUseCase {
    fun getIsSecondLaunch(): Flow<Boolean>
    suspend fun saveIsSecondLaunch(isSecondLaunch: Boolean)
}