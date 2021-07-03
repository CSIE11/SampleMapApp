package com.hclee.samplemapapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
                .getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        val seoul = LatLng(37.56, 126.97)
        val markerOptions = MarkerOptions().apply {
            position(seoul)
            title("seoul title")
            snippet("seoul snippet")
        }

        googleMap = map.apply {
            addMarker(markerOptions)
            moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 10f))
        }
    }
}
