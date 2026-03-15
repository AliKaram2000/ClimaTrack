package com.aeinae.climatrack.views.favourites

import com.aeinae.climatrack.data.local.database.entity.FavoriteEntity

sealed interface FavoritesUiState {
    data object Loading : FavoritesUiState
    data class Success(val favorites: List<FavoriteEntity>) : FavoritesUiState
}