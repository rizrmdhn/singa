package com.singa.asl.di

import com.singa.asl.MainActivityViewModel
import com.singa.asl.ui.MainAppViewModel
import com.singa.asl.ui.screen.change_password.ChangePasswordScreenViewModel
import com.singa.asl.ui.screen.conversation.ConversationViewModel
import com.singa.asl.ui.screen.history.HistoryScreenViewModel
import com.singa.asl.ui.screen.history_detail.HistoryDetailScreenViewModel
import com.singa.asl.ui.screen.home.HomeViewModel
import com.singa.asl.ui.screen.login.LoginScreenViewModel
import com.singa.asl.ui.screen.message.MessageScreenViewModel
import com.singa.asl.ui.screen.message_camera.MessageCameraViewModel
import com.singa.asl.ui.screen.onboarding.OnBoardingScreenViewModel
import com.singa.asl.ui.screen.profile.ProfileScreenViewModel
import com.singa.asl.ui.screen.profile_detail.ProfileDetailScreenViewModels
import com.singa.core.domain.usecase.SingaInteractor
import com.singa.core.domain.usecase.SingaUseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<SingaUseCase> { SingaInteractor(get()) }
}

val viewModelModule = module {
    viewModel { MessageCameraViewModel(get()) }
    viewModel { ConversationViewModel(get()) }
    viewModel { LoginScreenViewModel(get()) }
    viewModel { HistoryScreenViewModel(get()) }
    viewModel { MessageScreenViewModel(get()) }
    viewModel { ProfileScreenViewModel() }
    viewModel { ChangePasswordScreenViewModel() }
    viewModel { ProfileDetailScreenViewModels() }
    viewModel { OnBoardingScreenViewModel(get()) }
    viewModel { MainAppViewModel(get()) }
    viewModel { MainActivityViewModel(get()) }
    viewModel { HistoryDetailScreenViewModel(get()) }
    viewModel { HomeViewModel(get())}
}