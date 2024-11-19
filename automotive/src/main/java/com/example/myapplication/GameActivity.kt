package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import android.widget.FrameLayout
import android.content.Intent


class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // 기본적으로 공 추첨 레이아웃을 표시
        showBallLayout()

        // 공추첨 버튼 클릭 시
        val skinBallButton = findViewById<TextView>(R.id.skin_ball_Button)
        skinBallButton.setOnClickListener {
            showBallLayout() // 공 추첨 레이아웃 표시
            updateButtonStyles(skinBallButton, findViewById(R.id.skin_casino_Button))
        }

        // 슬롯머신 버튼 클릭 시
        val skinCasinoButton = findViewById<TextView>(R.id.skin_casino_Button)
        skinCasinoButton.setOnClickListener {
            showCasinoLayout() // 슬롯머신 레이아웃 표시
            updateButtonStyles(findViewById(R.id.skin_ball_Button), skinCasinoButton)
        }

        // 설정 버튼 클릭 시
        val settingsButton = findViewById<Button>(R.id.settingsButton)
        settingsButton.setOnClickListener {
            openSettingsScreen()
        }
    }

    // 공 추첨 레이아웃을 표시하는 메서드
    private fun showBallLayout() {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.layoutContainer, BallFragment())  // BallFragment로 교체
        fragmentTransaction.commit()
    }

    // 슬롯머신 레이아웃을 표시하는 메서드
    private fun showCasinoLayout() {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.layoutContainer, CasinoFragment())  // CasinoFragment로 교체
        fragmentTransaction.commit()
    }

    // 버튼 스타일 업데이트 (선택된 버튼 강조)
    private fun updateButtonStyles(selected: TextView, unselected: TextView) {
        selected.setTextColor(resources.getColor(R.color.selectedTextColor))
        selected.setBackgroundResource(R.drawable.toggle_button_left) // 선택된 버튼 배경

        unselected.setTextColor(resources.getColor(R.color.unselectedTextColor))
        unselected.setBackgroundResource(R.drawable.toggle_button_right) // 비선택된 버튼 배경
    }

    // 설정 화면으로 전환하는 메서드
    private fun openSettingsScreen() {
        val intent = Intent(this, SettingsActivity::class.java)
        this.startActivity(intent)
    }
}