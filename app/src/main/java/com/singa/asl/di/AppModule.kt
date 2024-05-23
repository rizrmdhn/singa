package com.singa.asl.di

import com.singa.asl.ui.MainAppViewModel
import com.singa.core.domain.usecase.SingaInteractor
import com.singa.core.domain.usecase.SingaUseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<SingaUseCase> { SingaInteractor(get()) }
}

val viewModelModule = module {
    viewModel { MainAppViewModel(get()) }
}