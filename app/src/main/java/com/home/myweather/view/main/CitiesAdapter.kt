package com.home.myweather.view.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.home.myweather.R
import com.home.myweather.model.Weather

class CitiesAdapter(val listener: OnMyItemClickListener) :
    RecyclerView.Adapter<CitiesAdapter.MainViewHolder>() {

    private var weatherData: List<Weather> = listOf()

    fun setWeather(data: List<Weather>) {
        this.weatherData = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CitiesAdapter.MainViewHolder {
        return MainViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.main_recycler_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CitiesAdapter.MainViewHolder, position: Int) {
        return holder.bind(this.weatherData[position])
    }

    override fun getItemCount(): Int {
        return weatherData.size
    }

    inner class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(weather: Weather) {
            with(itemView) {
                findViewById<AppCompatTextView>(R.id.mainFragmentRecyclerItemTextView).text =
                    weather.city.name
                setOnClickListener { listener.onItemClick(weather) }
            }
        }
    }
}