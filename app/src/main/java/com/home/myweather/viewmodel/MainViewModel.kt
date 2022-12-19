package com.home.myweather.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.home.myweather.repository.RepositoryCityListImpl
import java.lang.Thread.sleep

class MainViewModel : ViewModel() {

    private val liveData: MutableLiveData<AppState> by lazy { MutableLiveData() }

    fun getLiveData(): LiveData<AppState> = liveData
    private val repositoryCityListImpl: RepositoryCityListImpl by lazy {
        RepositoryCityListImpl()
    }

    fun getWeatherFromLocalSourceRus() = getWeatherFromServer(true)
    fun getWeatherFromLocalSourceWorld() = getWeatherFromServer(false)


    private fun getWeatherFromServer(isRussian: Boolean) {
        liveData.postValue(AppState.Loading(0))
        Thread {
            sleep(1000)
            val rand = (1..10).random()
            if (rand >= 2) {
                liveData.postValue(
                    AppState.Success(
                        with(repositoryCityListImpl) {
                            if (isRussian) {
                                getWeatherFromLocalStorageRus()
                            } else {
                                getWeatherFromLocalStorageWorld()
                            }
                        }
                    )
                )
            } else {
                liveData.postValue(AppState.Error(IllegalAccessException()))
            }
        }.start()
    }
}