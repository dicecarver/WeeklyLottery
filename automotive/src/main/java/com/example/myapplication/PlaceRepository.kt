package com.example.myapplication

import android.content.Context
import android.location.Location
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

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
        try {
            val inputStream = context.assets.open("modified_sorted_updated_csv_file.csv")
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
                    val firstPrizeCount = tokens[2].trim().toIntOrNull() ?: 0
                    val firstPrizeRounds = tokens[3].trim()
                    val secondPrizeCount = tokens[4].trim().toIntOrNull() ?: 0
                    val secondPrizeRounds = tokens[5].trim()
                    val latitude = tokens[6].trim().toDoubleOrNull() ?: 0.0
                    val longitude = tokens[7].trim().toDoubleOrNull() ?: 0.0

                    placeList.add(
                        Place(
                            name = name,
                            address = address,
                            firstPrizeCount = firstPrizeCount,
                            firstPrizeRounds = firstPrizeRounds,
                            secondPrizeCount = secondPrizeCount,
                            secondPrizeRounds = secondPrizeRounds,
                            latitude = latitude,
                            longitude = longitude,
                            distance = 0.0 // 초기 거리값은 필요 시 이후 계산
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
        filterMode: FilterMode = FilterMode.DISTANCE // 기본값: 거리 필터 모드
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

    // 필터 모드 열거형 추가
    enum class FilterMode {
        DISTANCE,    // 거리 기준
        RECOMMENDED  // 추천 기준 (거리 + 1등 당첨 횟수 정렬)
    }

}
