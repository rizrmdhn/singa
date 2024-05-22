package com.singa.asl.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Send
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val title: String,
    val iconFilled: ImageVector,
    val iconOutlined: ImageVector
)

val itemNavigation = listOf(
    NavigationItem(
        title = Screen.Home.route,
        iconFilled = Icons.Filled.Home,
        iconOutlined = Icons.Outlined.Home
    ),
    NavigationItem(
        title = Screen.Message.route,
        iconFilled = Icons.Filled.Send,
        iconOutlined = Icons.Outlined.Send
    ),
    NavigationItem(
        title = "WhiteSpace",
        iconFilled = Icons.Filled.Home,
        iconOutlined = Icons.Outlined.Home
    ),
    NavigationItem(
        title = Screen.History.route,
        iconFilled = Icons.Filled.List,
        iconOutlined = Icons.Outlined.List
    ),
    NavigationItem(
        title = Screen.Profile.route,
        iconFilled = Icons.Filled.AccountCircle,
        iconOutlined = Icons.Outlined.AccountCircle
    ),
)