package com.singa.asl.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.singa.asl.ui.components.TopBar
import com.singa.asl.ui.navigation.Screen
import com.singa.asl.ui.screen.conversation.ConversationScreen
import com.singa.asl.ui.screen.history.HistoryScreen
import com.singa.asl.ui.screen.home.HomeScreen
import com.singa.asl.ui.screen.message.MessageScreen
import com.singa.asl.ui.screen.onboarding.OnBoardingScreen
import com.singa.asl.ui.screen.realtime_camera.RealtimeCameraScreen
import com.singa.asl.ui.screen.web_view.WebViewScreen
import com.singa.asl.ui.screen.welcome.WelcomeScreen
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.ColorBackgroundWhite
import com.singa.asl.ui.theme.SingaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    navController: NavHostController = rememberNavController(),
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val isDisabledBottom = navBackStackEntry?.destination?.route in listOf(
        Screen.OnBoarding.route,
        Screen.Welcome.route,
        Screen.WebView.route,
        Screen.RealtimeCamera.route,
        Screen.Conversation.route
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
                startDestination = Screen.OnBoarding.route,
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
                    Screen.Welcome.route
                ) {
                    WelcomeScreen(
                        onNavigateToLogin = {
                            navController.navigate(Screen.WebView.route)
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
                    HistoryScreen()
                }

                composable(Screen.Profile.route) {
                    HomeScreen()
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