package com.weathersnap.data.repository

import com.weathersnap.data.db.WeatherDao
import com.weathersnap.data.db.WeatherReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val dao: WeatherDao
) {
    suspend fun saveReport(report: WeatherReport) = withContext(Dispatchers.IO) {
        dao.insertReport(report)
    }

    fun getAllReports(): Flow<List<WeatherReport>> = dao.getAllReports()
    
    fun getReportsCount(): Flow<Int> = dao.getReportsCount()
}
