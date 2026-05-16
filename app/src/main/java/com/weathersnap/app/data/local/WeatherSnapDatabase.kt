package com.weathersnap.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weathersnap.app.data.local.dao.ReportDraftDao
import com.weathersnap.app.data.local.dao.WeatherReportDao
import com.weathersnap.app.data.local.entity.ReportDraftEntity
import com.weathersnap.app.data.local.entity.WeatherReportEntity

@Database(
    entities = [WeatherReportEntity::class, ReportDraftEntity::class],
    version = 2,
    exportSchema = false
)
abstract class WeatherSnapDatabase : RoomDatabase() {
    abstract fun weatherReportDao(): WeatherReportDao
    abstract fun reportDraftDao(): ReportDraftDao
}
