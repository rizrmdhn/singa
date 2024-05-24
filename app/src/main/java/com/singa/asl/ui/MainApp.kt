package com.singa.asl.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.singa.asl.ui.components.BottomBar
import com.singa.asl.ui.components.FloatingButton
import com.singa.asl.ui.components.ModalNavigation
import com.singa.asl.ui.components.PopupAlertDialog
import com.singa.asl.ui.components.TopBar
import com.singa.asl.ui.navigation.Screen
import com.singa.asl.ui.screen.change_password.ChangePasswordScreen
import com.singa.asl.ui.screen.conversation.ConversationScreen
import com.singa.asl.ui.screen.history_detail.HistoryDetailScreen
import com.singa.asl.ui.screen.home.HomeScreen
import com.singa.asl.ui.screen.login.LoginScreen
import com.singa.asl.ui.screen.message.MessageScreen
import com.singa.asl.ui.screen.onboarding.OnBoardingScreen
import com.singa.asl.ui.screen.profile.ProfileScreen
import com.singa.asl.ui.screen.profile_detail.ProfileDetailScreen
import com.singa.asl.ui.screen.realtime_camera.RealtimeCameraScreen
import com.singa.asl.ui.screen.register.RegisterScreen
import com.singa.asl.ui.screen.web_view.WebViewScreen
import com.singa.asl.ui.screen.welcome.WelcomeScreen
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.ColorBackgroundWhite
import com.singa.asl.ui.theme.SingaTheme
import com.singa.core.domain.model.User
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    navController: NavHostController = rememberNavController(),
    authUser: User?,
    getAuthUser: () -> Unit,
    isSecondLaunch: Boolean,
    onLogouts: (
        navigateToWelcome: () -> Unit,
    ) -> Unit,
    logoutIsLoading: Boolean,
    alertDialog: Boolean,
    alertDialogTitle: String,
    alertDialogMessage: String,
    showDialog: (String, String) -> Unit,
    hideDialog: () -> Unit,
    viewModel: MainAppViewModel = koinViewModel()
) {
    val name by viewModel.name.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()


    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val isDisabledBottom = navBackStackEntry?.destination?.route in listOf(
        Screen.OnBoarding.route,
        Screen.Welcome.route,
        Screen.WebView.route,
        Screen.RealtimeCamera.route,
        Screen.Conversation.route,
        Screen.Login.route,
        Screen.Register.route,
        Screen.ProfileDetail.route,
        Screen.ChangePassword.route
    )

    val isDisabledTopBar = navBackStackEntry?.destination?.route in listOf(
        Screen.OnBoarding.route,
        Screen.Welcome.route,
        Screen.RealtimeCamera.route,
    )

    //modal sheet
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    val showBottomSheet = remember { mutableStateOf(false) }

    SingaTheme {
        Scaffold(
            topBar = {
                if (!isDisabledTopBar) {
                    TopBar(
                        name = authUser?.name ?: "",
                        avatarUrl = authUser?.avatar ?: "",
                        currentRoute = currentRoute,
                        navigateToProfile = {
                            navController.navigate(Screen.Profile.route)
                        },
                        navigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            },
            bottomBar = {
                if (!isDisabledBottom) {
                    BottomBar(
                        currentRoute = currentRoute,
                        navigateToScreen = {
                            navController.navigate(it) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            },
            floatingActionButton = {
                if (!isDisabledBottom) {
                    FloatingButton(
                        modalButtonNavigation = {
                            showBottomSheet.value = true
                        }
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            containerColor = if (!isDisabledTopBar) Color1 else Color.White,
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (authUser == null && !isSecondLaunch) {
                    Screen.OnBoarding.route
                } else if (authUser == null) {
                    Screen.Welcome.route
                } else {
                    Screen.Home.route
                },
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(
                    Screen.OnBoarding.route
                ) {
                    OnBoardingScreen(
                        onNavigateToWelcome = {
                            navController.navigate(Screen.Welcome.route)
                        }
                    )
                }

                composable(
                    Screen.Login.route
                ) {
                    LoginScreen(
                        email = email,
                        isEmailError = viewModel.validationState.emailError != null,
                        emailError = viewModel.validationState.emailError ?: "",
                        onChangeEmail = viewModel::onChangeEmail,
                        password = password,
                        isPasswordError = viewModel.validationState.passwordError != null,
                        passwordError = viewModel.validationState.passwordError ?: "",
                        onChangePassword = viewModel::onChangePassword,
                        isLoginLoading = viewModel.loginIsLoading,
                        onLogin = {
                            viewModel.onLogin(
                                getAuthUser = getAuthUser,
                                navigateToHome = {
                                    navController.navigate(Screen.Home.route)
                                },
                                showDialog = showDialog
                            )
                        },
                        navigateToRegister = {
                            viewModel.cleanEmail()
                            viewModel.cleanPassword()
                            viewModel.cleanValidationState()
                            navController.navigate(Screen.Register.route)
                        }
                    )
                }

                composable(
                    Screen.Register.route
                ) {
                    RegisterScreen(
                        name = name,
                        isNameError = viewModel.validationState.nameError != null,
                        nameError = viewModel.validationState.nameError ?: "",
                        onNameEmail = viewModel::onChangeName,
                        email = email,
                        isEmailError = viewModel.validationState.emailError != null,
                        emailError = viewModel.validationState.emailError ?: "",
                        onChangeEmail = viewModel::onChangeEmail,
                        password = password,
                        isPasswordError = viewModel.validationState.passwordError != null,
                        passwordError = viewModel.validationState.passwordError ?: "",
                        onChangePassword = viewModel::onChangePassword,
                        isRegisterLoading = viewModel.registerIsLoading,
                        onRegister = {
                            viewModel.onRegister(
                                navigateToLogin = {
                                    navController.navigate(Screen.Login.route)
                                },
                                showDialog = showDialog
                            )
                        },
                        navigateToLogin = {
                            viewModel.cleanName()
                            viewModel.cleanEmail()
                            viewModel.cleanPassword()
                            viewModel.cleanValidationState()
                            navController.navigate(Screen.Login.route)
                        }
                    )
                }

                composable(
                    Screen.Welcome.route
                ) {
                    WelcomeScreen(
                        onNavigateToLogin = {
                            navController.navigate(Screen.Login.route)
                        },
                        onNavigateToGuest = {
                            navController.navigate(Screen.Home.route)
                        }
                    )
                }

                composable(Screen.Home.route) {
                    HomeScreen()
                }

                composable(Screen.Message.route) {
                    MessageScreen(
                        onNavigateConversation = {
                            navController.navigate(Screen.Conversation.route)
                        }
                    )
                }

                composable(Screen.History.route) {
//                    HistoryScreen()
                    HistoryDetailScreen()
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(
                        avatarUrl = authUser?.avatar ?: "",
                        logoutIsLoading = logoutIsLoading,
                        onLogout = {
                            onLogouts {
                                navController.navigate(Screen.Welcome.route)
                            }
                        },
                        onNavigateToDetail = {
                            navController.navigate(Screen.ProfileDetail.route)
                        },
                        onNavigateToPassword = {
                            navController.navigate(Screen.ChangePassword.route)
                        }
                    )
                }

                composable(Screen.ProfileDetail.route) {
                    ProfileDetailScreen()
                }

                composable(Screen.ChangePassword.route) {
                    ChangePasswordScreen()
                }

                composable(Screen.WebView.route) {
                    WebViewScreen()
                }

                composable(
                    Screen.Conversation.route
                ) {
                    ConversationScreen()
                }

                composable(
                    Screen.RealtimeCamera.route
                ) {
                    RealtimeCameraScreen()
                }


            }

            if (alertDialog) {
                PopupAlertDialog(
                    title = alertDialogTitle,
                    text = alertDialogMessage,
                    onDismissRequest = hideDialog,
                    confirmButton = hideDialog
                )
            }

            if (showBottomSheet.value) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet.value = false },
                    containerColor = ColorBackgroundWhite,
                    sheetState = sheetState
                ) {
                    ModalNavigation(
                        navigateToRealtimeCamera = {
                            navController.navigate(Screen.RealtimeCamera.route)
                            showBottomSheet.value = false
                        },
                        navigateToConversation = {
                            navController.navigate(Screen.Conversation.route)
                            showBottomSheet.value = false
                        }
                    )
                }
            }
        }
    }
}