package com.home.myweather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.home.myweather.repository.RepositoryCityListImpl

class HistoryViewModel(private val liveData: MutableLiveData<AppState> = MutableLiveData()) :
    ViewModel() {

    private val repositoryCityListImpl: RepositoryCityListImpl by lazy {
        RepositoryCityListImpl()
    }

    fun getLiveData() = liveData

    fun getAllHistory() {
        Thread {
            val listWeather = repositoryCityListImpl.getAllHistoryWeather()
            liveData.postValue(AppState.Success(listWeather))
        }.start()
    }
}