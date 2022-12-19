package com.home.myweather.googleMaps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.home.myweather.R
import com.home.myweather.databinding.FragmentGoogleMapsMainBinding
import com.home.myweather.view.BaseFragment

class MapsFragment :
    BaseFragment<FragmentGoogleMapsMainBinding>(FragmentGoogleMapsMainBinding::inflate) {

    private lateinit var map: GoogleMap
    private val markers = arrayListOf<Marker>()

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        val m = LatLng(54.0, 37.0)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(m))
        googleMap.setOnMapLongClickListener {
            getAddress(it)
            addMarker(it)
            drawLine()
        }
        context?.let {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) -> {
                    googleMap.isMyLocationEnabled = true
                }
            }
            googleMap.uiSettings.isZoomControlsEnabled = true
        }
    }

    private fun drawLine() {
        val last = markers.size
        if (last > 1) {
            map.addPolyline(
                PolylineOptions().add(markers[last - 1].position, markers[last - 2].position)
                    .color(Color.RED)
                    .width(5f)
            )
        }
    }

    private fun addMarker(location: LatLng) {
        val marker = map.addMarker(
            MarkerOptions().position(location)
                .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))
        )
        markers.add(marker!!)
    }

    private fun getAddress(location: LatLng) {
        Thread {
            val geocoder = Geocoder(requireContext())
            val listAddress = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            requireActivity().runOnUiThread {
                binding.textAddress.text = listAddress!![0].getAddressLine(0)
            }
        }.start()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        binding.buttonSearch.setOnClickListener {
            search()
        }
    }

    private fun search() {
        Thread {
            val geocoder = Geocoder(requireContext())
            val listAddress =
                geocoder.getFromLocationName(binding.searchAddress.text.toString(), 1)
            requireActivity().runOnUiThread {
                map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            listAddress!![0].latitude,
                            listAddress[0].longitude
                        ), 10f
                    )
                )
                map.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            listAddress[0].latitude,
                            listAddress[0].longitude
                        )
                    ).title("").icon(
                        com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin)
                    )
                )
            }
        }.start()
    }
}