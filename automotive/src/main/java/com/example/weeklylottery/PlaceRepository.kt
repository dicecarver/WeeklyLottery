package com.example.weeklylottery

import android.content.Context
import android.location.Location
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.Period

// 1. csv 읽어오는 함수
// 2. 거리값 계산 및 리스트 정력(거리순 오름차순)
object PlaceRepository {
    private val TAG = "PlaceRepository"
    private var places: List<Place>? = null


    fun getPlaces(context: Context): List<Place> {
        if (places == null) {
            Log.d(TAG, "Loading CSV data for the first time...")
            places = loadPlacesFromCsv(context)
        } else {
            Log.d(TAG, "Returning cached CSV data")
        }
        return places!!
    }

    /**
     * CSV 데이터를 읽어와 List<Place>로 변환
     */
    private fun loadPlacesFromCsv(context: Context): List<Place> {
        val placeList = mutableListOf<Place>()
        val winDataMap = loadWinDataMap(context, "win_data_all.csv")
        val today = LocalDate.now()
        try {
            val inputStream = context.assets.open("stores_winning.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            var isFirstLine = true
            reader.forEachLine { line ->
                if (isFirstLine) {
                    isFirstLine = false
                    return@forEachLine
                }
                val tokens = line.split(",")
                if (tokens.size >= 8) {
                    val name = tokens[0].trim()
                    val address = tokens[1].trim()
                    val firstPrizeRounds = tokens[2].trim()
                    val firstPrizeCount = tokens[3].trim().toIntOrNull() ?: 0
                    val secondPrizeRounds = tokens[4].trim()
                    val secondPrizeCount = tokens[5].trim().toIntOrNull() ?: 0
                    val latitude = tokens[6].trim().toDoubleOrNull() ?: 0.0
                    val longitude = tokens[7].trim().toDoubleOrNull() ?: 0.0

                    // Calculate firstPrizeRecentText
                    val firstPrizeRecentText = firstPrizeRounds
                        .split(" ")
                        .mapNotNull { it.toIntOrNull() }
                        .maxOrNull()
                        ?.let { round ->
                            winDataMap[round]?.let { dateStr ->
                                val date = LocalDate.parse(dateStr)
                                getRelativeDateString(today, date)
                            } ?: "없음"
                        } ?: "없음"

                    // Calculate secondPrizeRecentText
                    val secondPrizeRecentText = secondPrizeRounds
                        .split(" ")
                        .mapNotNull { it.toIntOrNull() }
                        .maxOrNull()
                        ?.let { round ->
                            winDataMap[round]?.let { dateStr ->
                                val date = LocalDate.parse(dateStr)
                                getRelativeDateString(today, date)
                            } ?: "없음"
                        } ?: "없음"


                    // Add Place to the list
                    placeList.add(
                        Place(
                            name = name,
                            address = address,
                            firstPrizeCount = firstPrizeCount,
                            firstPrizeRounds = firstPrizeRounds,
                            firstPrizeRecentText = firstPrizeRecentText,
                            secondPrizeCount = secondPrizeCount,
                            secondPrizeRounds = secondPrizeRounds,
                            secondPrizeRecentText = secondPrizeRecentText,
                            latitude = latitude,
                            longitude = longitude,
                            distance = 0.0
                        )
                    )
                }
            }
            reader.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error reading CSV file", e)
        }
        Log.d(TAG, "Load CSV data complete")
        return placeList
    }


    // * 거리 필터를 적용하여 장소 목록 반환
    fun getPlacesFilteredByDistance(
        context: Context,
        currentLocation: Location,
        distance: Int,
        filterMode: FilterMode
    ): List<Place> {
        Log.d(TAG, "Filtering places by distance: $distance km")
        val cachedPlaces = getPlaces(context) // 캐시된 데이터 활용

        // 거리 계산 및 필터 적용
        val filteredPlaces = cachedPlaces.filter { place ->
            val placeLocation = Location("").apply {
                latitude = place.latitude
                longitude = place.longitude
            }
            val distanceToLocation = currentLocation.distanceTo(placeLocation) / 1000.0 // km 단위
            place.distance = distanceToLocation // 거리 업데이트
            distanceToLocation <= distance
        }

        return when (filterMode) {
            FilterMode.DISTANCE -> filteredPlaces // 거리 기준 필터만 적용
            FilterMode.RECOMMENDED -> {
                // 추천 기준: 1등 당첨 횟수 내림차순, 거리 오름차순
                //filteredPlaces.sortedWith(
                //    compareByDescending<Place> { it.firstPrizeCount }.thenBy { it.distance }
                //)
                filteredPlaces.sortedWith(
                    compareByDescending<Place> { it.firstPrizeCount }
                        .thenByDescending { it.secondPrizeCount }
                        .thenBy { it.distance } // 거리값 오름차순 (작을수록 먼저)
                )
            }
        }
    }

    private fun loadWinDataMap(context: Context, fileName: String): Map<Int, String> {
        val map = mutableMapOf<Int, String>()
        try {
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.useLines { lines ->
                lines.drop(1).forEach { line ->
                    val tokens = line.split(",")
                    if (tokens.size > 10) {
                        val round = tokens[0].toIntOrNull()
                        val date = tokens[10].trim()
                        if (round != null) {
                            map[round] = date
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading win data file", e)
        }
        return map
    }

    private fun getRelativeDateString(today: LocalDate, targetDate: LocalDate): String {
        val period = Period.between(targetDate, today)
        return when {
            period.years >= 1 -> "${period.years}년 전"
            period.months >= 1 -> "${period.months}개월 전"
            period.days >= 7 -> "${period.days / 7}주 전"
            else -> "${period.days}일 전"
        }
    }

    // 필터 모드 열거형 추가
    enum class FilterMode {
        DISTANCE,    // 거리 기준
        RECOMMENDED  // 추천 기준 (거리 + 1등 당첨 횟수 정렬)
    }

}
