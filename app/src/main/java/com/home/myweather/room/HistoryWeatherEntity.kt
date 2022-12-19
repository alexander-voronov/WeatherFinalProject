package com.home.myweather.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_for_history_weather")
data class HistoryWeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val city: String,
    val temperature: Int,
    val feelsLike: Int,
    val icon: String
)