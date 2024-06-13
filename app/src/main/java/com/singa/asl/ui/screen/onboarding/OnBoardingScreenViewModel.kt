package com.singa.asl.ui.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.singa.core.domain.usecase.SingaUseCase
import kotlinx.coroutines.launch

class OnBoardingScreenViewModel (
    private val singaUseCase: SingaUseCase
): ViewModel() {
    fun setIsSecondLaunch() {
        viewModelScope.launch {
            singaUseCase.saveIsSecondLaunch(
                isSecondLaunch = true
            )
        }
    }
}