package com.home.myweather.view.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.home.myweather.BUNDLE_KEY
import com.home.myweather.R
import com.home.myweather.databinding.FragmentMainBinding
import com.home.myweather.model.City
import com.home.myweather.model.Weather
import com.home.myweather.view.details.DetailsFragment
import com.home.myweather.viewmodel.AppState
import com.home.myweather.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

class MainFragment : Fragment(), OnMyItemClickListener {
    private val MIN_DISTANCE = 100f
    private val REFRESH_PERIOD = 60000L
    private val REQUEST_CODE_MAIN_FRAGMENT = 25

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
        get() {
            return _binding!!
        }
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private val adapter: CitiesAdapter by lazy { CitiesAdapter(this) }
    private var isRussian = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        viewModel.getLiveData().observe(viewLifecycleOwner, Observer<AppState> { renderData(it) })
        viewModel.getWeatherFromLocalSourceRus()
    }

    private fun initView() {
        with(binding) {
            mainFragmentRecyclerView.adapter = adapter
            mainFragmentFAB.setOnClickListener {
                sentRequest()
            }
            mainFragmentFABLocation.setOnClickListener {
                checkPermission()
            }
        }
    }

    private fun checkPermission() {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    getLocation()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    showDialogRatio()
                }
                else -> {
                    myRequestPermission()
                }
            }
        }
    }

    private fun getAddress(location: Location) {
        Thread {
            val geocoder = Geocoder(requireContext())
            val listAddress = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            requireActivity().runOnUiThread {
                showAddressDialog(
                    listAddress[0].getAddressLine(0),
                    location
                )
            }
        }.start()
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            getAddress(location)
        }

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    }

    private fun myRequestPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE_MAIN_FRAGMENT
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_MAIN_FRAGMENT) {
            when {
                (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> {
                    getLocation()
                }

                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    showDialogRatio()
                }
            }
        }
    }

    private fun showDialogRatio() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_rationale_title)
            .setMessage(R.string.dialog_message_no_gps)
            .setPositiveButton(R.string.dialog_rationale_give_access) { _, _ -> myRequestPermission() }
            .setNegativeButton(R.string.dialog_rationale_decline) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun getLocation() {
        activity?.let {
            if (ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val locationManager =
                    it.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    val providerGPS = locationManager.getProvider(LocationManager.GPS_PROVIDER)
                    providerGPS?.let {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            REFRESH_PERIOD,
                            MIN_DISTANCE,
                            locationListener
                        )
                    }
                } else {
                    val lastLocation =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    lastLocation?.let {
                        getAddress(it)
                    }
                }
            }
        }
    }

    private fun showAddressDialog(address: String, location: Location) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_address_title)
            .setPositiveButton(getString(R.string.dialog_address_get_weather)) { _, _ ->
                toDetails(
                    Weather(
                        City(address, location.latitude, location.longitude)
                    )
                )
            }
            .setNegativeButton(R.string.dialog_rationale_decline) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun sentRequest() {
        isRussian = !isRussian
        with(binding) {
            with(viewModel) {
                if (isRussian) {
                    getWeatherFromLocalSourceRus()
                    mainFragmentFAB.setImageResource(R.drawable.ic_russia)
                } else {
                    getWeatherFromLocalSourceWorld()
                    mainFragmentFAB.setImageResource(R.drawable.ic_earth)
                }
            }
        }
    }

    private fun renderData(appState: AppState) {
        with(binding) {
            when (appState) {
                is AppState.Error -> {
                    mainFragmentLoadingLayout.visibility = View.GONE
                    root.setWithoutAction(
                        (R.string.error),
                        (R.string.try_again),
                        { sentRequest() },
                        Snackbar.LENGTH_LONG
                    )
                }
                is AppState.Loading -> mainFragmentLoadingLayout.visibility = View.VISIBLE
                is AppState.Success -> {
                    mainFragmentLoadingLayout.visibility = View.GONE
                    adapter.setWeather(appState.weatherData)
                    root.withoutAction(R.string.success, Snackbar.LENGTH_LONG)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClick(weather: Weather) {
        toDetails(weather)
    }

    private fun toDetails(weather: Weather) {
        activity?.run {
            supportFragmentManager.beginTransaction()
                .add(R.id.main_activity_container, DetailsFragment.newInstance(Bundle().apply {
                    putParcelable(BUNDLE_KEY, weather)
                }))
                .addToBackStack("").commit()
        }
    }

    private fun View.withoutAction(text: Int, leinghtShow: Int) {
        Snackbar.make(this, text, leinghtShow).show()
    }

    private fun View.setWithoutAction(
        text: Int,
        actionText: Int,
        action: (View) -> Unit,
        leinghtShow: Int
    ) {
        Snackbar.make(this, text, leinghtShow).setAction(actionText, action).show()
    }
}