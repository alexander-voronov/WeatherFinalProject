package com.home.myweather.repository

import com.home.myweather.model.Weather

interface RepositoryCityList {
    fun getWeatherFromLocalStorageRus(): List<Weather>
    fun getWeatherFromLocalStorageWorld(): List<Weather>
}