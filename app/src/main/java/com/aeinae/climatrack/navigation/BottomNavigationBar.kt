package com.aeinae.climatrack.navigation

import androidx.compose.foundation.border
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aeinae.climatrack.ui.theme.ClimaTrackTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Favourites,
    BottomNavItem.Alerts,
    BottomNavItem.Settings
)

@Composable
fun BottomNavigationBar(navController: NavHostController, enabled: Boolean){

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    //val currentRoute = navBackStackEntry?.destination?.route
    val currentRoute by remember {
        derivedStateOf { navBackStackEntry?.destination?.route }
    }
    val alpha: Float = 0.05F
    NavigationBar(
        containerColor = ClimaTrackTheme.extendedColors.bottomNavBackground,
        modifier = Modifier.border(
            width = 0.5.dp,
            color = ClimaTrackTheme.extendedColors.divider
        )
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { navController.navigate(item.route){
                    navController.graph.startDestinationRoute?.let { route ->
                        popUpTo(route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                        }
                } },
                icon = @Composable{ Icon(
                    contentDescription = item.label,
                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon
                )},
                enabled = enabled,
                label = @Composable{Text(text = item.label)},
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ClimaTrackTheme.extendedColors.bottomNavActive,
                    unselectedIconColor = ClimaTrackTheme.extendedColors.bottomNavInactive,
                    selectedTextColor = ClimaTrackTheme.extendedColors.bottomNavActive,
                    unselectedTextColor = ClimaTrackTheme.extendedColors.bottomNavInactive,
                    indicatorColor = ClimaTrackTheme.extendedColors.bottomNavActive.copy(alpha = alpha)
                )
            )
        }
    }
}