package com.singa.asl.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.singa.asl.R
import com.singa.asl.ui.navigation.Screen
import com.singa.asl.ui.navigation.itemNavigation
import com.singa.asl.ui.theme.Color1


@Composable
fun BottomBar(
    navController: NavHostController, navBackStackEntry: NavBackStackEntry?
) {
    NavigationBar(
        containerColor = colorResource(id = R.color.white)
    ) {
        itemNavigation.forEach { items ->
            val isSelected =
                items.title == (navBackStackEntry?.destination?.route ?: Screen.Home.route)
            NavigationBarItem(
                modifier = Modifier.padding(vertical = 20.dp),
                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color1,
                    unselectedIconColor = Color(0xFFC6D8FC),
                    indicatorColor = Color.White
                ),
                onClick = {
                    navController.navigate(items.title) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = {
                    if (items.title != "WhiteSpace") {
                        Icon(
                            imageVector = if (isSelected) items.iconFilled else items.iconOutlined,
                            contentDescription = items.title,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
            )
        }
    }
}