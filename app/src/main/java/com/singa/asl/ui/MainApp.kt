package com.singa.asl.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.singa.asl.ui.navigation.Screen
import com.singa.asl.ui.screen.onboarding.OnBoardingScreen
import com.singa.asl.ui.theme.SingaTheme

@Composable
fun MainApp(
    navController: NavHostController = rememberNavController(),
) {
    SingaTheme {
        Scaffold { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.OnBoarding.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(
                    route = Screen.OnBoarding.route
                ) {
                    OnBoardingScreen()
                }
            }
        }
    }
}