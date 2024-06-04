package com.singa.asl.ui

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.singa.asl.common.ValidationState
import com.singa.asl.ui.components.BottomBar
import com.singa.asl.ui.components.FloatingButton
import com.singa.asl.ui.components.ModalNavigation
import com.singa.asl.ui.components.PopupAlertDialog
import com.singa.asl.ui.components.TopBar
import com.singa.asl.ui.navigation.Screen
import com.singa.asl.ui.screen.change_password.ChangePasswordScreen
import com.singa.asl.ui.screen.conversation.ConversationScreen
import com.singa.asl.ui.screen.history.HistoryScreen
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
    onUpdateProfile: (
        context: Context,
        uri: Uri?,
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        isSignUser: Boolean,
        updateValidationState: (validationState: ValidationState) -> Unit,
        clearChangePasswordForm: () -> Unit,
        setUpdateIsLoading: (status: Boolean) -> Unit
    ) -> Unit,
    alertDialog: Boolean,
    alertDialogTitle: String,
    alertDialogMessage: String,
    showDialog: (String, String) -> Unit,
    hideDialog: () -> Unit,
    saveAccessToken: (String) -> Unit,
    saveRefreshToken: (String) -> Unit,
    context: Context = LocalContext.current,
    viewModel: MainAppViewModel = koinViewModel()
) {
    val socialLoginUrl by viewModel.socialLoginUrl.collectAsState()

    val name by viewModel.name.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val isSignUser by viewModel.isSignUser.collectAsState()


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
        Screen.ChangePassword.route,
        Screen.HistoryDetail.route
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

    LaunchedEffect(key1 = currentRoute) {
        if (currentRoute == Screen.ProfileDetail.route) {
            viewModel.onChangeName(authUser?.name ?: "")
            viewModel.onChangeEmail(authUser?.email ?: "")
            viewModel.setSignUser(authUser?.isSignUser ?: false)
        }
    }


    LaunchedEffect(socialLoginUrl) {
        if (socialLoginUrl.isNotEmpty()) {
            navController.navigate(Screen.WebView.route)
//            uriHandler.openUri(socialLoginUrl)
//            viewModel.clearSocialLoginUrl()
        }
    }

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
                        resetForm = viewModel::resetForm,
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
                        setSocialLoginUrl = viewModel::setSocialLoginUrl,
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
                        onLoginAsGuest = {
                            // this is for testing real time camera (cameraX)
//                            val permissionCheckResult =
//                                ContextCompat.checkSelfPermission(
//                                    context,
//                                    Manifest.permission.CAMERA
//                                )
//                            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
//                                navController.navigate(Screen.TestCamera.route)
//                            } else {
//                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
//                            }
                            viewModel.onLoginAsGuest(
                                getAuthUser = getAuthUser,
                                navigateToHome = {
                                    navController.navigate(Screen.Home.route)
                                },
                                showDialog = showDialog
                            )
                        }
                    )
                }

                composable(Screen.Home.route) {
                    HomeScreen(
                        showModal = {
                            showBottomSheet.value = true
                        }
                    )
                }

                composable(Screen.Message.route) {
                    MessageScreen(
                        onNavigateConversation = {
                            navController.navigate(
                                Screen.Conversation.createRoute(it.toString())
                            )
                        }
                    )
                }

                composable(Screen.History.route) {
                    HistoryScreen(
                        navigateToDetail = {
                            navController.navigate(Screen.HistoryDetail.createRoute(it))
                        }
                    )
                }

                composable(
                    route = Screen.HistoryDetail.route,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) {
                    val id = it.arguments?.getString("id") ?: "0"
                    HistoryDetailScreen(
                        id = id.toInt()
                    )
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(
                        avatarUrl = authUser?.avatar ?: "",
                        logoutIsLoading = logoutIsLoading,
                        onLogout = {
                            if (authUser?.email == null) {
                                it(
                                    "Logout",
                                    "Are you sure you want to logout?. make sure you update your email and password first otherwise you will lose your account"
                                )
                            } else {
                                it("Logout", "Are you sure you want to logout?")
                            }
                        },
                        onConfirmLogout = {
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
                    ProfileDetailScreen(
                        avatarUrl = authUser?.avatar ?: "",
                        name = name,
                        isNameError = viewModel.validationState.nameError != null,
                        nameError = viewModel.validationState.nameError ?: "",
                        email = email,
                        isEmailError = viewModel.validationState.emailError != null,
                        emailError = viewModel.validationState.emailError ?: "",
                        onChangeName = viewModel::onChangeName,
                        onChangeEmail = viewModel::onChangeEmail,
                        resetForm = viewModel::resetForm,
                        isSignUser = isSignUser,
                        onChangeIsSignUser = viewModel::onChangeSignUser,
                        navigateBack = {
                            navController.popBackStack()
                        },
                        onUpdate = { uri, setLoadingState ->
                            onUpdateProfile(
                                context,
                                uri,
                                name,
                                email,
                                password,
                                confirmPassword,
                                isSignUser,
                                viewModel::updateValidationState,
                                viewModel::clearPasswordAndConfirmPassword,
                                setLoadingState
                            )
                        }
                    )
                }

                composable(Screen.ChangePassword.route) {
                    ChangePasswordScreen(
                        password = password,
                        onChangePassword = viewModel::onChangePassword,
                        isPasswordError = viewModel.validationState.passwordError != null,
                        passwordError = viewModel.validationState.passwordError ?: "",
                        confirmPassword = confirmPassword,
                        onChangeConfirmPassword = viewModel::onChangeConfirmPassword,
                        isConfirmPasswordError = viewModel.validationState.confirmPasswordError != null,
                        confirmPasswordError = viewModel.validationState.confirmPasswordError ?: "",
                        navigateBack = {
                            navController.popBackStack()
                        },
                        resetForm = viewModel::clearPasswordAndConfirmPassword,
                        onUpdatePassword = { setLoadingState ->
                            if (password != confirmPassword) {
                                viewModel.updateValidationState(
                                    ValidationState(
                                        passwordError = "Password not match",
                                        confirmPasswordError = "Password not match"
                                    )
                                )
                                return@ChangePasswordScreen
                            }

                            onUpdateProfile(
                                context,
                                Uri.EMPTY,
                                name,
                                email,
                                password,
                                confirmPassword,
                                isSignUser,
                                viewModel::updateValidationState,
                                viewModel::clearPasswordAndConfirmPassword,
                                setLoadingState
                            )
                        }
                    )
                }

                composable(Screen.WebView.route) {
                    WebViewScreen(
                        url = socialLoginUrl,
                        clearSocialLoginUrl = viewModel::clearSocialLoginUrl,
                        saveAccessToken = saveAccessToken,
                        saveRefreshToken = saveRefreshToken,
                        getAuthUser = getAuthUser,
                        navigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(
                    route = Screen.Conversation.route,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { args ->
                    val id = args.arguments?.getString("id") ?: "0"
                    ConversationScreen(
                        id = id.toInt(),
                        onNavigateVideo = {
                            navController.navigate(Screen.HistoryDetail.createRoute(it.toString()))
                        }
                    )
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