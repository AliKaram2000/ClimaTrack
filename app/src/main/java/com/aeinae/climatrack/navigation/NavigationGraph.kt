package com.aeinae.climatrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aeinae.climatrack.views.AlertsScreen
import com.aeinae.climatrack.views.favourites.FavoritesScreen
import com.aeinae.climatrack.views.favourites.add.AddFavoriteScreen
import com.aeinae.climatrack.views.favourites.details.FavoriteDetailScreen
import com.aeinae.climatrack.views.home.HomeScreen
import com.aeinae.climatrack.views.map.MapPickerScreen
import com.aeinae.climatrack.views.settings.SettingsScreen

@Composable
fun NavigationHost(modifier: Modifier, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {

        composable(BottomNavItem.Home.route) { HomeScreen() }

        composable(BottomNavItem.Favourites.route) {
            FavoritesScreen(
                onFavoriteClick = { id -> navController.navigate(Routes.favouriteDetail(id)) },
                onAddClick = { navController.navigate(Routes.ADD_FAVOURITE) }
            )
        }

        composable(BottomNavItem.Alerts.route) { AlertsScreen() }

        composable(BottomNavItem.Settings.route) {
            SettingsScreen(
                onNavigateToMapPicker = { navController.navigate(Routes.MAP_PICKER) }
            )
        }


        composable(Routes.MAP_PICKER) {
            MapPickerScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ADD_FAVOURITE) {
            AddFavoriteScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.FAVOURITE_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable
            FavoriteDetailScreen(
                favoriteId = id,
                onBack = { navController.popBackStack() }
            )
        }
    }
}