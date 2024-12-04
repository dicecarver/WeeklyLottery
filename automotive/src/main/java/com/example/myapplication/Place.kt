package com.example.myapplication

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Place(
    val name: String,
    val address: String,
    val firstPrizeCount: Int,
    val firstPrizeRounds: String,
    val secondPrizeCount: Int,
    val secondPrizeRounds: String,
    val latitude: Double,
    val longitude: Double,
    val additionalInfo: String = "",
    var distance: Double = 0.0 // 거리 필드 추가
) : Parcelable