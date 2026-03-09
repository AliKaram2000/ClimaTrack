package com.aeinae.climatrack.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val alertType: String,
    val startTime: Long,
    val endTime: Long,
    val notificationType: String,
    val isEnabled: Boolean = true,
    )