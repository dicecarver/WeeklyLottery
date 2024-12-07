package com.example.myapplication

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MotionEvent
import android.widget.ImageView
import com.bumptech.glide.Glide

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        /////////////////////////
        // gif 재생을 위해 코드 추가
        val lotteryAnimationView = findViewById<ImageView>(R.id.lotteryAnimationView)
        // Glide를 사용하여 GIF를 로드
        Glide.with(this)
            .asGif()
            .load(R.drawable.weeklylottery_splash_animation)
            .into(lotteryAnimationView)
        ////////////////////////////

        // ImageView의 알파값을 서서히 증가시키는 애니메이션 추가
        animateFadeIn(lotteryAnimationView)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            navigateToMainActivity() // 터치 시 MainActivity로 이동
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish() // 스플래시 화면 종료
    }

    // 알파값을 0에서 1로 변경하는 애니메이션 함수
    private fun animateFadeIn(view: ImageView) {
        view.alpha = 0f // 초기 알파값 설정
        val fadeInAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1.5f) // 알파값을 0에서 1로 애니메이션
        fadeInAnimator.duration = 3000 // 애니메이션 지속 시간 (밀리초)
        fadeInAnimator.start()
    }
}