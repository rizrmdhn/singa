package com.singa.asl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.singa.asl.ui.MainApp
import com.singa.core.data.Resource
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {
    private val viewModels: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val splashScreen = installSplashScreen()

        setContent {
            val isScreenReady by viewModels.isScreenReady.collectAsState()
            val isSecondLaunch by viewModels.isSecondLaunch.collectAsState()
            val logoutIsLoading by viewModels.logoutIsLoading.collectAsState()

            val alertDialog by viewModels.alertDialog.collectAsState()
            val alertDialogTitle by viewModels.alertDialogTitle.collectAsState()
            val alertDialogMessage by viewModels.alertDialogMessage.collectAsState()

            splashScreen.setKeepOnScreenCondition {
                !isScreenReady
            }

            viewModels.authUser.collectAsState(initial = Resource.Loading()).value.let { state ->
                when (state) {
                    is Resource.Loading -> {
                        viewModels.setScreenNotReady()
                    }

                    is Resource.Empty -> {
                        viewModels.setScreenReady()
                        MainApp(
                            onLogouts = viewModels::logout,
                            authUser = null,
                            getAuthUser = viewModels::getAuthUser,
                            logoutIsLoading = logoutIsLoading,
                            isSecondLaunch = isSecondLaunch,
                            onUpdateProfile = viewModels::updateUser,
                            alertDialog = alertDialog,
                            alertDialogTitle = alertDialogTitle,
                            alertDialogMessage = alertDialogMessage,
                            showDialog = viewModels::showAlert,
                            hideDialog = viewModels::hideAlert,
                            saveAccessToken = viewModels::saveAccessToken,
                            saveRefreshToken = viewModels::saveRefreshToken
                        )
                    }

                    is Resource.Success -> {
                        viewModels.setScreenReady()
                        MainApp(
                            onLogouts = viewModels::logout,
                            authUser = state.data,
                            getAuthUser = viewModels::getAuthUser,
                            logoutIsLoading = logoutIsLoading,
                            isSecondLaunch = isSecondLaunch,
                            onUpdateProfile = viewModels::updateUser,
                            alertDialog = alertDialog,
                            alertDialogTitle = alertDialogTitle,
                            alertDialogMessage = alertDialogMessage,
                            showDialog = viewModels::showAlert,
                            hideDialog = viewModels::hideAlert,
                            saveAccessToken = viewModels::saveAccessToken,
                            saveRefreshToken = viewModels::saveRefreshToken
                        )
                    }

                    is Resource.Error -> {
                        viewModels.setScreenReady()
                        MainApp(
                            onLogouts = viewModels::logout,
                            authUser = null,
                            getAuthUser = viewModels::getAuthUser,
                            logoutIsLoading = logoutIsLoading,
                            isSecondLaunch = isSecondLaunch,
                            onUpdateProfile = viewModels::updateUser,
                            alertDialog = alertDialog,
                            alertDialogTitle = alertDialogTitle,
                            alertDialogMessage = alertDialogMessage,
                            showDialog = viewModels::showAlert,
                            hideDialog = viewModels::hideAlert,
                            saveAccessToken = viewModels::saveAccessToken,
                            saveRefreshToken = viewModels::saveRefreshToken,
                        )
                    }

                    is Resource.ValidationError -> {
                        viewModels.setScreenReady()
                        MainApp(
                            onLogouts = viewModels::logout,
                            authUser = null,
                            getAuthUser = viewModels::getAuthUser,
                            logoutIsLoading = logoutIsLoading,
                            isSecondLaunch = isSecondLaunch,
                            onUpdateProfile = viewModels::updateUser,
                            alertDialog = alertDialog,
                            alertDialogTitle = alertDialogTitle,
                            alertDialogMessage = alertDialogMessage,
                            showDialog = viewModels::showAlert,
                            hideDialog = viewModels::hideAlert,
                            saveAccessToken = viewModels::saveAccessToken,
                            saveRefreshToken = viewModels::saveRefreshToken
                        )
                    }
                }
            }
        }
    }
}

