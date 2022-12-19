package com.home.myweather.room

import androidx.room.*

@Dao
interface HistoryWeatherDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entity: HistoryWeatherEntity)

    @Delete
    fun delete(entity: HistoryWeatherEntity)

    @Update
    fun update(entity: HistoryWeatherEntity)

    @Query("SELECT * FROM table_for_history_weather")
    fun getAllHistoryWeather(): List<HistoryWeatherEntity>
}