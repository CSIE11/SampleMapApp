package com.hclee.samplemapapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {

        private const val TAG = "MapsActivity"
        private val REQUIRED_PERMISSIONS = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val UPDATE_INTERVAL = 1000L
        private const val FASTEST_UPDATE_INTERVAL = 500L
        private const val REQUEST_CODE_PERMISSIONS = 1
    }

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var location: Location
    private lateinit var currentLocation: Location
    private lateinit var currentPosition: LatLng
    private var currentMarker: Marker? = null
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            val locationList = locationResult.locations

            if (locationList.isNotEmpty()) {
                location = locationList.last()
                currentPosition = LatLng(location.latitude, location.longitude)

                val markerTitle = "current position marker title"
                val markerSnippet = "current position marker snippet"

                setCurrentLocation(location, markerTitle, markerSnippet)
            }
        }

        override fun onLocationAvailability(p0: LocationAvailability) {
            super.onLocationAvailability(p0)
        }
    }

    private fun setCurrentLocation(location: Location, markerTitle: String, markerSnippet: String) {
        val currentLatLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions().apply {
            position(currentLatLng)
            title(markerTitle)
            snippet(markerSnippet)
            draggable(true)
        }
        val cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng)

        currentLocation = location
        currentMarker = googleMap.addMarker(markerOptions)

        googleMap.moveCamera(cameraUpdate)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
                .getMapAsync(this)

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL)

        LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        setDefaultLocation()

        val deniedPermissions = REQUIRED_PERMISSIONS.map { permission ->
            permission to (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED)
        }.filter { it.second == false }.map { it.first }.toTypedArray()

        if (deniedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_PERMISSIONS)
        } else {
            startLocationUpdates()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "permission not granted")
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )

        googleMap.isMyLocationEnabled = true
    }

    private fun checkLocationServicesStatus() =
        (getSystemService(LOCATION_SERVICE) as LocationManager).run {
            isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            val hasAllGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

            if (hasAllGranted) {
                startLocationUpdates()
            } else {

            }
        }
    }

    private fun setDefaultLocation() {
        val seoul = LatLng(37.56, 126.97)
        val markerOptions = MarkerOptions().apply {
            position(seoul)
            title("seoul title")
            snippet("seoul snippet")
        }

        googleMap.also {
            it.addMarker(markerOptions)
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 10f))
        }
    }
}
