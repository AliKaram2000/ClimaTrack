package com.aeinae.climatrack.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aeinae.climatrack.data.local.database.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity)

    @Query("SELECT * FROM alerts")
    fun getAllAlerts(): Flow<List<AlertEntity>>

    @Delete
    suspend fun delete(alert: AlertEntity)

    @Query("UPDATE alerts SET isEnabled = :isEnabled WHERE id = :alertID")
    suspend fun update(alertID: Int, isEnabled: Boolean)

    @Query("SELECT * FROM alerts WHERE isEnabled = 1 AND endTime > :currentTime")
    suspend fun getActiveAlerts(currentTime: Long): List<AlertEntity>

}