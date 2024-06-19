package com.singa.asl.ui.navigation

sealed class Screen(val route: String) {
    data object OnBoarding : Screen("onboarding")
    data object Welcome : Screen("welcome")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Profile : Screen("profile")
    data object ProfileDetail : Screen("profile_detail")
    data object ChangePassword : Screen("change_password")
    data object Message : Screen("conversation")
    data object Conversation : Screen("conversation/{id}") {
        fun createRoute(id: String) = "conversation/$id"
    }
    data object ConversationDetail : Screen("conversation_detail/{translationId}/{conversationId}") {
        fun createRoute(translationId: String, conversationId: String) = "conversation_detail/$translationId/$conversationId"
    }
    data object History : Screen("translation")
    data object HistoryDetail : Screen("history_detail/{id}") {
        fun createRoute(id: String) = "history_detail/$id"
    }
    data object HistoryCamera : Screen("historycamera/{title}") {
        fun createRoute(title: String) = "historycamera/$title"
    }
    data object WebView : Screen("webview")
    data object RealtimeCamera : Screen("realtimecamera")
    data object MessageCamera : Screen("messagecamera/{id}") {
        fun createRoute(id: String) = "messagecamera/$id"
    }
}