package com.example.weeklylottery.interfaces

import com.example.weeklylottery.Place

interface MapViewUpdateListener {
    fun onMapUpdated(latitude: Double, longitude: Double)
}
interface PlaceClickListener {
    fun onPlaceClick(place: Place)
}