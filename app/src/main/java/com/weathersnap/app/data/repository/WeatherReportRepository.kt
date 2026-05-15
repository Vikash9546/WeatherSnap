package com.weathersnap.app.data.repository

import android.content.Context
import com.weathersnap.app.data.local.dao.WeatherReportDao
import com.weathersnap.app.data.local.entity.WeatherReportEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherReportRepository @Inject constructor(
    private val dao: WeatherReportDao,
    @ApplicationContext private val context: Context
) {
    fun getAllReports(): Flow<List<WeatherReportEntity>> = dao.getAllReports()

    suspend fun saveReport(report: WeatherReportEntity): Long =
        withContext(Dispatchers.IO) {
            dao.insertReport(report)
        }
}
