package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class NaviPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private var selectedPlaceTab0: Place? = null // 추천순 탭에서 선택된 장소
    private var selectedPlaceTab1: Place? = null // 거리순 탭에서 선택된 장소

    override fun getItemCount(): Int = 2 // 탭 개수

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NaviRecommendFragment()
            1 -> NaviDistanceFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
    // 추천순 탭에서 선택된 장소 설정 및 반환
    fun setSelectedPlaceForTab0(place: Place?) {
        selectedPlaceTab0 = place
    }

    fun getSelectedPlaceForTab0(): Place? = selectedPlaceTab0

    // 거리순 탭에서 선택된 장소 설정 및 반환
    fun setSelectedPlaceForTab1(place: Place?) {
        selectedPlaceTab1 = place
    }

    fun getSelectedPlaceForTab1(): Place? = selectedPlaceTab1
}

