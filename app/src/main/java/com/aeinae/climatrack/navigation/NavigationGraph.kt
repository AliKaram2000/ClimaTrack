package com.aeinae.climatrack.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aeinae.climatrack.views.AlertsScreen
import com.aeinae.climatrack.views.favourites.FavoritesScreen
import com.aeinae.climatrack.views.home.HomeScreen
import com.aeinae.climatrack.views.SettingsScreen
import androidx.compose.ui.Modifier

@Composable
fun NavigationHost(modifier: Modifier, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Home.route) { HomeScreen() }
        composable(BottomNavItem.Favourites.route) { FavoritesScreen(
            onFavoriteClick = { Log.d("NavigationHost", "onFavoriteClick: $it") },
            onAddClick = {Log.d("NavigationHost", "onAddClick")}
        ) }
        composable(BottomNavItem.Alerts.route) { AlertsScreen() }
        composable(BottomNavItem.Settings.route) { SettingsScreen() }
    }
}
