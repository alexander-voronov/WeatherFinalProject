package com.home.myweather.repository

import com.home.myweather.model.City
import com.home.myweather.model.Weather
import com.home.myweather.model.getRussianCities
import com.home.myweather.model.getWorldCities
import com.home.myweather.room.HistoryWeatherEntity
import com.home.myweather.view.MyApp

class RepositoryCityListImpl : RepositoryCityList, RepositoryHistoryWeather {
    override fun getWeatherFromLocalStorageRus() = getRussianCities()

    override fun getWeatherFromLocalStorageWorld() = getWorldCities()

    override fun getAllHistoryWeather(): List<Weather> {
        return convertHistoryWeatherEntityToWeather(
            MyApp.getHistoryWeatherDao().getAllHistoryWeather()
        )
    }

    override fun saveWeather(weather: Weather) {
        Thread {
            MyApp.getHistoryWeatherDao().insert(convertWeatherToHistoryWeatherEntity(weather))
        }.start()
    }

    private fun convertHistoryWeatherEntityToWeather(entityList: List<HistoryWeatherEntity>): List<Weather> {
        return entityList.map {
            Weather(
                City(it.city, 0.0, 0.0), it.temperature, it.feelsLike, it.icon
            )
        }
    }

    private fun convertWeatherToHistoryWeatherEntity(weather: Weather) =
        HistoryWeatherEntity(
            0,
            weather.city.name,
            weather.temperature,
            weather.feelsLike,
            weather.icon
        )
}