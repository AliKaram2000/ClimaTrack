package com.aeinae.climatrack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aeinae.climatrack.screens.AlertsScreen
import com.aeinae.climatrack.screens.FavouriteScreen
import com.aeinae.climatrack.screens.HomeScreen
import com.aeinae.climatrack.screens.SettingsScreen
import androidx.compose.ui.Modifier

@Composable
fun NavigationHost(modifier: Modifier, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route
    ) {
        composable(BottomNavItem.Home.route) { HomeScreen() }
        composable(BottomNavItem.Favourites.route) { FavouriteScreen() }
        composable(BottomNavItem.Alerts.route) { AlertsScreen() }
        composable(BottomNavItem.Settings.route) { SettingsScreen() }
    }
}
