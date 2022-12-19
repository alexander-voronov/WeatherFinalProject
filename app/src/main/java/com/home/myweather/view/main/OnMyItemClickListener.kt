package com.home.myweather.view.main

import com.home.myweather.model.Weather

interface OnMyItemClickListener {
    fun onItemClick(weather: Weather)
}