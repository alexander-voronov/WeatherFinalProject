package com.home.myweather.repository


import com.home.myweather.YANDEX_API_URL
import com.home.myweather.model.WeatherDTO
import com.home.myweather.view.MyApp.Companion.retrofit
import com.google.gson.GsonBuilder
import com.home.myweather.BuildConfig
import retrofit2.Callback
import retrofit2.converter.gson.GsonConverterFactory

class RepositoryDetailsImpl : RepositoryDetails {
    override fun getWeatherFromServer(lat: Double, lon: Double, callback: Callback<WeatherDTO>) {
        retrofit.let {
            it.baseUrl(YANDEX_API_URL)
            it.addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setLenient().create()
                )
            )
                .build().create(WeatherApi::class.java)
        }.getWeather(BuildConfig.WEATHER_API_KEY, lat, lon).enqueue(callback)
    }
}