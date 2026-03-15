package com.aeinae.climatrack.data.repository

import com.aeinae.climatrack.data.local.database.dao.FavoriteDao
import com.aeinae.climatrack.data.local.database.entity.FavoriteEntity
import com.aeinae.climatrack.data.source.local.WeatherLocalDataSource
import com.aeinae.climatrack.utils.Constants
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(
    private val favoriteDao: FavoriteDao,
    private val weatherLocalDataSource: WeatherLocalDataSource
) {

    fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return favoriteDao.getAllFavorites()
    }

    suspend fun addFavorite(favorite: FavoriteEntity) {
        favoriteDao.insertFavorite(favorite)
    }

    suspend fun deleteFavorite(favorite: FavoriteEntity) {
        favoriteDao.delete(favorite)
        weatherLocalDataSource.deleteCachedWeather(Constants.favoriteCacheKey(favorite.id))
    }
}