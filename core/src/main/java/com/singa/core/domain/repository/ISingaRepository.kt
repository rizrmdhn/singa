package com.singa.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface ISingaRepository {
    fun getIsSecondLaunch(): Flow<Boolean>
    suspend fun saveIsSecondLaunch(isFirstLaunch: Boolean)
}