package com.home.myweather.repository

import com.home.myweather.model.Weather

interface RepositoryHistoryWeather {
    fun getAllHistoryWeather(): List<Weather>
    fun saveWeather(weather: Weather)
}