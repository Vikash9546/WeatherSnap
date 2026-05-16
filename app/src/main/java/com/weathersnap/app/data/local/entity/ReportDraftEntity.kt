package com.weathersnap.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Draft entity for lifecycle-safe draft recovery.
 * Only one draft exists at a time (singleton draft via id=1).
 */
@Entity(tableName = "report_drafts")
data class ReportDraftEntity(
    @PrimaryKey
    val id: Int = 1, // Singleton draft
    val cityName: String,
    val condition: String,
    val temperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Double,
    val weatherCode: Int,
    val notes: String,
    val imagePath: String?,
    val originalSize: Long?,
    val compressedSize: Long?,
    val updatedAt: Long = System.currentTimeMillis()
)
