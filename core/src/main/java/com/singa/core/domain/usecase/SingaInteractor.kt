package com.singa.core.domain.usecase

import com.singa.core.domain.repository.ISingaRepository
import kotlinx.coroutines.flow.Flow

class SingaInteractor(
    private val singaRepository: ISingaRepository
): SingaUseCase {
    override fun register(email: String, password: String) = singaRepository.register(email, password)

    override fun login(email: String, password: String) = singaRepository.login(email, password)

    override fun logout() = singaRepository.logout()

    override fun guest() = singaRepository.guest()

    override fun getMe() = singaRepository.getMe()

    override fun updateMe(name: String, email: String, avatar: String) = singaRepository.updateMe(name, email, avatar)

    override fun updateToken(refreshToken: String) = singaRepository.updateToken(refreshToken)

    override fun getAccessToken() = singaRepository.getAccessToken()

    override suspend fun saveAccessToken(accessToken: String) = singaRepository.saveAccessToken(accessToken)

    override suspend fun removeAccessToken() = singaRepository.removeAccessToken()

    override fun getRefreshToken() = singaRepository.getRefreshToken()

    override suspend fun saveRefreshToken(refreshToken: String) = singaRepository.saveRefreshToken(refreshToken)

    override suspend fun removeRefreshToken() = singaRepository.removeRefreshToken()

    override fun getIsSecondLaunch() = singaRepository.getIsSecondLaunch()

    override suspend fun saveIsSecondLaunch(isSecondLaunch: Boolean) = singaRepository.saveIsSecondLaunch(isSecondLaunch)

}