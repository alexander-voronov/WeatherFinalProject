package com.home.myweather.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.home.myweather.R
import com.home.myweather.TAG_E
import com.home.myweather.model.Weather
import com.home.myweather.model.WeatherDTO
import com.home.myweather.model.getDefaultCity
import com.home.myweather.repository.RepositoryCityListImpl
import com.home.myweather.repository.RepositoryDetailsImpl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class DetailsViewModel(
    private val liveData: MutableLiveData<AppState> = MutableLiveData(),
    private val repositoryCityListImpl: RepositoryCityListImpl = RepositoryCityListImpl()
) :
    ViewModel() {

    private val repositoryDetailsImpl: RepositoryDetailsImpl by lazy {
        RepositoryDetailsImpl()
    }

    fun saveWeather(weather: Weather) {
        repositoryCityListImpl.saveWeather(weather)
    }

    fun getLiveData() = liveData

    fun getWeatherFromServer(lat: Double, lon: Double) {
        repositoryDetailsImpl.getWeatherFromServer(lat, lon, callback)
    }

    fun converterDTOtoModel(weatherDTO: WeatherDTO): List<Weather> {
        return listOf(
            Weather(
                getDefaultCity(),
                weatherDTO.fact.temp.toInt(),
                weatherDTO.fact.feelsLike.toInt(),
                weatherDTO.fact.icon
            )
        )
    }

    private val callback = object : Callback<WeatherDTO> {
        override fun onFailure(call: Call<WeatherDTO>, t: Throwable) {
            throw IOException(R.string.IOE_exception.toString())
        }

        override fun onResponse(call: Call<WeatherDTO>, response: Response<WeatherDTO>) {
            if (response.isSuccessful) {
                response.body()?.let {
                    liveData.postValue(AppState.Success(converterDTOtoModel(it)))
                }
            } else {
                when (response.code()) {
                    400 -> Log.e(TAG_E, R.string.bad_request.toString())
                    401 -> Log.e(TAG_E, R.string.unauthorized.toString())
                    402 -> Log.e(TAG_E, R.string.payment_required.toString())
                    403 -> Log.e(TAG_E, R.string.forbidden.toString())
                    404 -> Log.e(TAG_E, R.string.not_found.toString())
                }
            }
        }
    }
}