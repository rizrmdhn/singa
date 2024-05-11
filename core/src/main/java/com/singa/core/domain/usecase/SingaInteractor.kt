package com.singa.core.domain.usecase

import com.singa.core.domain.repository.ISingaRepository

class SingaInteractor(
    private val singaRepository: ISingaRepository
): SingaUseCase {
    override fun getIsSecondLaunch() = singaRepository.getIsSecondLaunch()

    override suspend fun saveIsSecondLaunch(isSecondLaunch: Boolean) = singaRepository.saveIsSecondLaunch(isSecondLaunch)
}