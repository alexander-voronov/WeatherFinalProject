package com.home.myweather.repository

import com.home.myweather.model.WeatherDTO

interface RepositoryDetails {
    fun getWeatherFromServer(lat: Double, lon: Double, callback: retrofit2.Callback<WeatherDTO>)
}