package dev.rafaelsermenho.kmmweatherdemo

import dev.rafaelsermenho.kmmweathershared.entity.Response
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import dev.rafaelsermenho.kmmweatherdemo.databinding.ActivityMainBinding
import dev.rafaelsermenho.kmmweathershared.api.OpenWeatherAPI
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity(), LocationListener {
    private lateinit var geoCoder: Geocoder
    private lateinit var locationManager: LocationManager
    private lateinit var binding: ActivityMainBinding
    private val minTimeInMsToUpdate: Long = 1000
    private val minDistanceInMToUpdate: Float = 1000f
    private val permissionRequestCode = 1024
    private val mainScope = MainScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupLocationManager()
        setupGeoCoder()
    }

    override fun onResume() {
        super.onResume()
        requestLocationUpdates()
    }

    override fun onLocationChanged(location: Location) {
        val address =
            geoCoder.getFromLocation(location.latitude, location.longitude, 1)
        binding.cityDescription.text = address[0].subAdminArea
        mainScope.launch {
            kotlin.runCatching {
                OpenWeatherAPI().getWeatherForCity(binding.cityDescription.text as String)
            }.onSuccess { updateWeatherData(it) }.onFailure {
                it.message?.let { error ->
                    Log.d(
                        "myTagErro",
                        error
                    )
                }
            }
        }
    }

    private fun updateWeatherData(response: Response) {
        binding.wheatherData.text =
            "Hoje vamos ter ${response.weather[0].description} e uma temperatura de ${response.main.temp} graus!"
    }

    private fun setupLocationManager() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun setupGeoCoder() {
        geoCoder = Geocoder(this, Locale.getDefault())
    }

    private fun requestLocationUpdates() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTimeInMsToUpdate,
                    minDistanceInMToUpdate,
                    this
                )
            }
            else -> {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    permissionRequestCode
                )
            }
        }
    }
}
