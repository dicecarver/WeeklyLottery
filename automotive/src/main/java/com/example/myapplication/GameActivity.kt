package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import android.content.Intent
import android.media.Image
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatButton
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val tabLayout_game: TabLayout = findViewById(R.id.tabLayout_game)
        val viewPager_game: ViewPager2 = findViewById(R.id.layoutContainer_game)
        val viewPager_setting: FrameLayout = findViewById(R.id.settingsFragmentContainer)
        val settingsButton = findViewById<ImageButton>(R.id.settingsButton)

        // 기본적으로 공 추첨 레이아웃을 표시

        // ViewPager에 Adapter 연결
        val adapter_game = GamePagerAdapter(this)
        viewPager_game.adapter = adapter_game

        // TabLayout과 ViewPager 연결
        TabLayoutMediator(tabLayout_game, viewPager_game) { tab, position ->
            tab.text = when (position) {
                0 -> "        BALL SKIN        "
                1 -> "        MARKING SKIN        "
                else -> null
            }
            // Tab 클릭 시 설정 화면을 종료하고 ViewPager로 전환
            tab.view.setOnClickListener {
                // 설정 화면 종료
                viewPager_setting.visibility = View.GONE
                viewPager_game.visibility = View.VISIBLE
                settingsButton.visibility = View.VISIBLE

                // 선택된 Tab으로 ViewPager 페이지 전환
                viewPager_game.currentItem = position
            }
        }.attach()

        val backButton: AppCompatButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val settingsContainer: FrameLayout = findViewById(R.id.settingsFragmentContainer)
            val viewPager: ViewPager2 = findViewById(R.id.layoutContainer_game)

            // 현재 화면이 SettingsFragment인지 확인
            if (settingsContainer.visibility == View.VISIBLE) {

                setInitialAlpha(tabLayout_game, 1.0f)
                enableTabLayout(tabLayout_game, true)

                // 설정 화면 종료
                settingsContainer.visibility = View.GONE
                viewPager.visibility = View.VISIBLE
                settingsButton.visibility = View.VISIBLE

                supportFragmentManager.popBackStack()
            } else {
                // 기본 동작: onBackPressedDispatcher 호출
                super.onBackPressed()
                //onBackPressedDispatcher.onBackPressed()
            }
        }


        // 설정 버튼 클릭 시
        settingsButton.setOnClickListener {
            setInitialAlpha(tabLayout_game, 0.4f)
            enableTabLayout(tabLayout_game, false)
            showSettingsScreen(viewPager_setting, viewPager_game, settingsButton)
        }
    }

    private fun showFragment(fragment: androidx.fragment.app.Fragment, containerId: Int) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(containerId, fragment)
        fragmentTransaction.commit()
    }

    // 설정 화면을 표시하는 메서드
    private fun showSettingsScreen(
        settingsContainer: FrameLayout,
        viewPager: ViewPager2,
        settingsButton: ImageButton
    ) {
        settingsContainer.visibility = View.VISIBLE
        viewPager.visibility = View.GONE
        settingsButton.visibility = View.INVISIBLE

        // SettingsFragment 표시
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.settingsFragmentContainer, SettingsFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        val settingsContainer: FrameLayout = findViewById(R.id.settingsFragmentContainer)
        val viewPager: ViewPager2 = findViewById(R.id.layoutContainer_game)
        val settingsButton: ImageButton = findViewById(R.id.settingsButton)
        val tabLayout_game: TabLayout = findViewById(R.id.tabLayout_game)

        if (settingsContainer.visibility == View.VISIBLE) {

            setInitialAlpha(tabLayout_game, 1.0f)
            enableTabLayout(tabLayout_game, true)

            settingsContainer.visibility = View.GONE
            viewPager.visibility = View.VISIBLE
            settingsButton.visibility = View.VISIBLE

            // BackStack에서 설정 화면 제거
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    private fun setInitialAlpha(view: View, alpha: Float) {
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                setInitialAlpha(view.getChildAt(i), alpha)
            }
            view.alpha = alpha
        } else {
            view.alpha = alpha
        }
    }

    private fun enableTabLayout(tabLayout: TabLayout, isEnabled: Boolean) {
        tabLayout.isEnabled = isEnabled
        tabLayout.isClickable = isEnabled
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)?.view
            tab?.isEnabled = isEnabled
            tab?.isClickable = isEnabled
        }
    }
}