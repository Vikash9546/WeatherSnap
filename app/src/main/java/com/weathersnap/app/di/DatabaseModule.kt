package com.weathersnap.app.di

import android.content.Context
import androidx.room.Room
import com.weathersnap.app.data.local.WeatherSnapDatabase
import com.weathersnap.app.data.local.dao.ReportDraftDao
import com.weathersnap.app.data.local.dao.WeatherReportDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WeatherSnapDatabase =
        Room.databaseBuilder(
            context,
            WeatherSnapDatabase::class.java,
            "weather_snap.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideWeatherReportDao(db: WeatherSnapDatabase): WeatherReportDao =
        db.weatherReportDao()

    @Provides
    fun provideReportDraftDao(db: WeatherSnapDatabase): ReportDraftDao =
        db.reportDraftDao()
}
