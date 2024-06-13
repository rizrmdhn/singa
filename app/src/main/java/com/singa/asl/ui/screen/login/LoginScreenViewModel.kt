package com.singa.asl.ui.screen.login

import androidx.lifecycle.ViewModel
import com.singa.core.domain.usecase.SingaUseCase
import kotlinx.coroutines.flow.MutableStateFlow

class LoginScreenViewModel(
    private val singaUseCase: SingaUseCase
): ViewModel() {

    private val _githubLoginIsLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val githubLoginIsLoading = _githubLoginIsLoading

    private val _googleLoginIsLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val googleLoginIsLoading = _googleLoginIsLoading

    fun setGithubLoginIsLoading(isLoading: Boolean) {
        _githubLoginIsLoading.value = isLoading
    }

    fun setGoogleLoginIsLoading(isLoading: Boolean) {
        _googleLoginIsLoading.value = isLoading
    }
}