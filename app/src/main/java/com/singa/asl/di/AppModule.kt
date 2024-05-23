package com.singa.asl.di

import com.singa.asl.MainActivityViewModel
import com.singa.asl.ui.MainAppViewModel
import com.singa.asl.ui.screen.onboarding.OnBoardingScreenViewModel
import com.singa.core.domain.usecase.SingaInteractor
import com.singa.core.domain.usecase.SingaUseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<SingaUseCase> { SingaInteractor(get()) }
}

val viewModelModule = module {
    viewModel { OnBoardingScreenViewModel(get()) }
    viewModel { MainAppViewModel(get()) }
    viewModel { MainActivityViewModel(get()) }
}