package com.example.myapplication.interfaces

import com.example.myapplication.Place

interface MapViewUpdateListener {
    fun onMapUpdated(latitude: Double, longitude: Double)
}
interface PlaceClickListener {
    fun onPlaceClick(place: Place)
}