package com.home.myweather.view.details

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import coil.request.ImageRequest
import com.home.myweather.BUNDLE_KEY

import com.home.myweather.model.Weather
import com.home.myweather.view.BaseFragment
import com.home.myweather.viewmodel.AppState
import com.home.myweather.viewmodel.DetailsViewModel
import com.google.android.material.snackbar.Snackbar
import com.home.myweather.R
import com.home.myweather.databinding.FragmentDetailsBinding
import kotlinx.android.synthetic.main.fragment_details.*

class DetailsFragment : BaseFragment<FragmentDetailsBinding>(FragmentDetailsBinding::inflate) {

    private lateinit var localWeather: Weather

    private val viewModel: DetailsViewModel by lazy {
        ViewModelProvider(this).get(DetailsViewModel::class.java)
    }

    companion object {
        fun newInstance(bundle: Bundle) = DetailsFragment().apply { arguments = bundle }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getLiveData().observe(viewLifecycleOwner, { renderData(it) })
        arguments?.let {
            it.getParcelable<Weather>(BUNDLE_KEY)?.let {
                localWeather = it
                viewModel.getWeatherFromServer(localWeather.city.lat, localWeather.city.lon)
            }
        }
    }

    private fun renderData(appState: AppState) {
        with(binding) {
            when (appState) {
                is AppState.Error -> {
                    loadingLayout.visibility = View.GONE
                    root.withoutAction(R.string.error, Snackbar.LENGTH_LONG)
                }
                is AppState.Loading -> {
                }
                is AppState.Success -> {
                    val weather = appState.weatherData[0]
                    setWeatherData(weather)
                }
            }
        }
    }

    private fun setWeatherData(weather: Weather) {
        with(binding) {
            weather.city = localWeather.city
            viewModel.saveWeather(weather)
            with(localWeather) {
                cityName.text = city.name
                cityCoordinates.text =
                    "${city.lat} ${city.lon}"
                temperatureValue.text = "${weather.temperature}"
                feelsLikeValue.text = "${weather.feelsLike}"
            }

        }

        headerIcon.load(R.drawable.city_pictures)
        weatherIcon.loadUrl("https://yastatic.net/weather/i/icons/funky/dark/${weather.icon}.svg")
    }

    private fun View.withoutAction(text: Int, leinghtShow: Int) {
        Snackbar.make(this, text, leinghtShow).show()
    }

    private fun ImageView.loadUrl(url: String) {

        val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry { add(SvgDecoder(this@loadUrl.context)) }
            .build()

        val request = ImageRequest.Builder(this.context)
            .data(url)
            .target(this)
            .build()

        imageLoader.enqueue(request)
    }
}
