package com.singa.asl.ui.navigation

sealed class Screen(val route: String) {
    data object OnBoarding : Screen("onboarding")
    data object Welcome : Screen("welcome")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Main : Screen("main")
    data object Home : Screen("home")
    data object Profile : Screen("profile")
    data object Message : Screen("message")
    data object Conversation : Screen("conversation")
    data object History : Screen("history")
    data object HistoryDetail : Screen("history_detail")
    data object WebView : Screen("webview")
    data object RealtimeCamera : Screen("realtimecamera")
    data object MessageCamera : Screen("messagecamera")
}