package com.singa.core.data

import com.singa.core.data.source.local.LocalDataSource
import com.singa.core.domain.repository.ISingaRepository
import kotlinx.coroutines.flow.Flow

class SingaRepository(
    private val localDataSource: LocalDataSource
): ISingaRepository {
    override fun getIsSecondLaunch(): Flow<Boolean> {
        return localDataSource.getIsSecondLaunch()
    }

    override suspend fun saveIsSecondLaunch(isFirstLaunch: Boolean) {
        localDataSource.saveIsSecondLaunch(isFirstLaunch)
    }
}