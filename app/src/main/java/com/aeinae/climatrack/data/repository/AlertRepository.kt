package com.aeinae.climatrack.data.repository

import com.aeinae.climatrack.data.local.database.dao.AlertDao
import com.aeinae.climatrack.data.local.database.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

class AlertRepository(private val dao: AlertDao) {

    suspend fun addAlert(alert: AlertEntity){
        dao.insertAlert(alert)
    }
    suspend fun updateAlert(id: Int, isEnabled: Boolean){
        dao.update(id, isEnabled)
    }
    suspend fun delete(alert: AlertEntity){
        dao.delete(alert)
    }
    fun getAllAlerts(): Flow<List<AlertEntity>>{
        return dao.getAllAlerts()
    }
    suspend fun getActiveAlerts(currentTime: Long): List<AlertEntity>{
       return dao.getActiveAlerts(currentTime)
    }
}