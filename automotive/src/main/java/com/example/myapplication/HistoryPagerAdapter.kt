package com.example.myapplication

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class HistoryPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2 // 탭 개수

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HistoryDateFragment.newInstance()
            1 -> HistoryNumbersFragment.newInstance()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
