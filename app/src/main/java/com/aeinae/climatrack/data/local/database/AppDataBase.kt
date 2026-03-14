package com.aeinae.climatrack.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aeinae.climatrack.data.local.database.dao.AlertDao
import com.aeinae.climatrack.data.local.database.dao.FavoriteDao
import com.aeinae.climatrack.data.local.database.dao.WeatherCacheDao
import com.aeinae.climatrack.data.local.database.entity.AlertEntity
import com.aeinae.climatrack.data.local.database.entity.FavoriteEntity
import com.aeinae.climatrack.data.local.database.entity.WeatherCacheEntity
import com.aeinae.climatrack.utils.Constants.DATABASE_NAME
import com.aeinae.climatrack.utils.Constants.DATABASE_VERSION

@Database(entities = [WeatherCacheEntity::class, FavoriteEntity::class, AlertEntity::class], version = DATABASE_VERSION, exportSchema = false)
abstract class AppDataBase: RoomDatabase() {

    abstract fun weatherCacheDao(): WeatherCacheDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun alertDao(): AlertDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    DATABASE_NAME)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}