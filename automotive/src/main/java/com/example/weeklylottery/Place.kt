package com.example.weeklylottery

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Place(
    val name: String,
    val address: String,
    val firstPrizeCount: Int,
    val firstPrizeRounds: String,
    val firstPrizeRecentText: String,
    val secondPrizeCount: Int,
    val secondPrizeRounds: String,
    val secondPrizeRecentText: String,
    val latitude: Double,
    val longitude: Double,
    val additionalInfo: String = "",
    var distance: Double = 0.0 // 거리 필드 추가
) : Parcelable