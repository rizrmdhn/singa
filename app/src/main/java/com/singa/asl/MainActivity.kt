package com.singa.asl

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.singa.asl.ui.MainApp
import com.singa.core.data.Resource
import com.singa.core.domain.model.User
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

            splashScreen.setKeepOnScreenCondition {
                !isScreenReady
            }

            viewModels.authUser.collectAsState(initial = Resource.Loading()).value.let { state ->
                when (state) {
                    is Resource.Loading -> {
                        viewModels.setScreenNotReady()
                    }

                    is Resource.Success -> {
                        viewModels.setScreenReady()
                        MainApp(
                            onLogout = viewModels::logout,
                            authUser = state.data,
                            isSecondLaunch = isSecondLaunch
                        )
                    }

                    is Resource.Error -> {
                        viewModels.setScreenReady()
                        MainApp(
                            onLogout = viewModels::logout,
                            authUser = null,
                            isSecondLaunch = isSecondLaunch
                        )
                    }

                    is Resource.ValidationError -> {
                        viewModels.setScreenReady()
                        MainApp(
                            onLogout = viewModels::logout,
                            authUser = null,
                            isSecondLaunch = isSecondLaunch
                        )
                    }
                }
            }
        }
    }
}

