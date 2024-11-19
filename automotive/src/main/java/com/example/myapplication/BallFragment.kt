package com.example.myapplication

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.media.SoundPool
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper

import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.BufferedReader
import kotlin.random.Random

class BallFragment : Fragment() {

    private lateinit var mixingBallMediaPlayer: MediaPlayer // nullable로 선언하여 안전성 강화
    private lateinit var buttonMediaPlayer: MediaPlayer // nullable로 선언하여 안전성 강화
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var ballLayout: FrameLayout
    private lateinit var lefthandButton: ImageButton
    private lateinit var righthandButton: ImageButton

    private lateinit var circleText1: TextView
    private lateinit var circleText2: TextView
    private lateinit var circleText3: TextView
    private lateinit var circleText4: TextView
    private lateinit var circleText5: TextView
    private lateinit var circleText6: TextView

    private lateinit var soundPool: SoundPool
    private var rollSoundId: Int = -1
    private var collisionSoundId: Int = -1
    private var rollSoundStreamId: Int = -1

    // 변수를 선언하여 쉽게 조정할 수 있도록 설정
    private var rotationCount = 5 // 회전 횟수
    private var rotationSpeed = 3000 // 1초당 1바퀴
    private var circleSize = 100 // 원 크기
    private var borderWidth = 10 // 테두리 굵기
    private var numberText = "7" // 원 안의 숫자
    private var startX = 1300f // 시작 위치 (화면 왼쪽)
    private var endX = 0f  // 끝 위치 (화면 오른쪽)

    // 이동 속도 (이 변수로 이동 시간을 조정할 수 있음)
    private var moveSpeed = 3000 // 이동 속도, 단위는 밀리초 (작을수록 빠름)

    private var isRunning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.skin_ball, container, false)

        // mixingBallMediaPlayer 초기화
        mixingBallMediaPlayer = MediaPlayer.create(requireContext(), R.raw.mixing_ball_sound)
        buttonMediaPlayer = MediaPlayer.create(requireContext(), R.raw.press_button_sound)
        buttonMediaPlayer.setVolume(0.3f, 0.3f)
        //mixingBallMediaPlayer.isLooping = true

        // Find the views and initialize them
        circleText1 = rootView.findViewById(R.id.circleText1)
        circleText2 = rootView.findViewById(R.id.circleText2)
        circleText3 = rootView.findViewById(R.id.circleText3)
        circleText4 = rootView.findViewById(R.id.circleText4)
        circleText5 = rootView.findViewById(R.id.circleText5)
        circleText6 = rootView.findViewById(R.id.circleText6)
        // TextView 배열을 통해 circleText1 ~ circleText6을 참조

        ballLayout = rootView.findViewById(R.id.ballLayout) // skin_ball.xml에서 ballLayout

        lefthandButton = rootView.findViewById(R.id.lefthandButton)
        righthandButton = rootView.findViewById(R.id.righthandButton)

        updateCircle()

        // 원을 처음에는 보이지 않도록 설정
        circleText1.visibility = View.INVISIBLE
        circleText2.visibility = View.INVISIBLE
        circleText3.visibility = View.INVISIBLE
        circleText4.visibility = View.INVISIBLE
        circleText5.visibility = View.INVISIBLE
        circleText6.visibility = View.INVISIBLE

        // 좌측 버튼 클릭 이벤트
        lefthandButton.setOnClickListener {
            if (isRunning) return@setOnClickListener
            isRunning = true
            disableButtons()

            animateHandButton(lefthandButton, righthandButton, moveToRight = true) {
                // Start 버튼을 누르는 동작 수행
                startLottoDraw()
            }
        }

        // 우측 버튼 클릭 이벤트
        righthandButton.setOnClickListener {
            if (isRunning) return@setOnClickListener
            isRunning = true
            disableButtons()

            animateHandButton(righthandButton, lefthandButton, moveToRight = false) {
                // Start 버튼을 누르는 동작 수행
                startLottoDraw()
            }
        }

        // SoundPool 초기화
        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .build()

        // 소리 로드
        rollSoundId = soundPool.load(context, R.raw.roll_sound, 1)  // 구르는 소리
        collisionSoundId = soundPool.load(context, R.raw.collision_sound, 1)  // 공 부딪히는 소리

        return rootView
    }

    private fun startLottoDraw() {

        val circleTextViews = listOf(circleText1, circleText2, circleText3, circleText4, circleText5, circleText6)

        var numberWeights = IntArray(45) { 50 } // 기본값
        val balls = (1..45).toMutableList() // 1부터 45까지의 공 리스트
        val selectedNumbers = mutableListOf<Int>()
        val initialWeights = readWeightsFromCsv() // CSV 파일에서 가중치 불러오기 (없으면 기본값 50 설정)
        var weights = initialWeights.toMutableList() // 가중치 리스트 복사

        circleText1.visibility = View.INVISIBLE
        circleText2.visibility = View.INVISIBLE
        circleText3.visibility = View.INVISIBLE
        circleText4.visibility = View.INVISIBLE
        circleText5.visibility = View.INVISIBLE
        circleText6.visibility = View.INVISIBLE

        println("[my_log]weight : $weights")
        println("[my_log]weight sum : ${weights.sum()}")

        repeat(6) {

            val cumulativeSum = weights.sum()

            // 남아있는 가중치의 합이 0인 경우 모든 가중치를 1로 설정
            if (cumulativeSum == 0.0) {
                println("[my_log]남아있는 공의 가중치가 모두 0입니다. 모든 공의 가중치를 1로 설정합니다.")
                weights = List(balls.size) { 1.0 }.toMutableList() // 남은 공의 가중치를 모두 1로 설정
            }

            // 누적 가중치를 계산
            val cumulativeWeights = mutableListOf<Double>()
            var cumulativeTemp = 0.0
            for (weight in weights) {
                cumulativeTemp += weight
                cumulativeWeights.add(cumulativeTemp)
            }

            // 누적 가중치 범위 내에서 랜덤 값을 생성해 공 선택
            val randomValue = Random.nextDouble(cumulativeWeights.last())
            val index = cumulativeWeights.indexOfFirst { it >= randomValue }

            val selectedBall = balls[index]
            println("[my_log]$selectedBall")
            selectedNumbers.add(selectedBall)

            // 선택된 공과 해당 가중치를 제거하여 다시 뽑히지 않도록 처리
            weights.removeAt(index)
            balls.removeAt(index)
        }

        println("[my_log]추출된 숫자 (순서대로): $selectedNumbers")

        for (i in selectedNumbers.indices) {
            val number = selectedNumbers[i]
            val textView = circleTextViews[i]

            // TextView에 숫자를 설정
            textView.text = number.toString()

            // 숫자 범위에 따른 배경 설정
            when (number) {
                in 1..10 -> textView.setBackgroundResource(R.drawable.circle_background_1_10)
                in 11..20 -> textView.setBackgroundResource(R.drawable.circle_background_11_20)
                in 21..30 -> textView.setBackgroundResource(R.drawable.circle_background_21_30)
                in 31..40 -> textView.setBackgroundResource(R.drawable.circle_background_31_40)
                in 41..45 -> textView.setBackgroundResource(R.drawable.circle_background_41_45)
            }
        }
        // mixing_ball_sound의 첫 10초 구간을 반복 재생 시작
        buttonMediaPlayer.start() // Play button click sound

        // Delay playMixSound by 0.5 seconds
        handler.postDelayed({
            playMixSound(0) // Start mixing sound after delay
        }, 500) // 500 milliseconds = 0.5 seconds

        Handler(Looper.getMainLooper()).postDelayed({
            startRotationAndMoveAnimation(circleText1, 0, 50f - 300f) {
                Handler(Looper.getMainLooper()).postDelayed({
                    startRotationAndMoveAnimation(circleText2, 1, 50f - 200f) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            startRotationAndMoveAnimation(circleText3, 2, 50f - 100f) {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    startRotationAndMoveAnimation(circleText4, 3, 50f) {
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            startRotationAndMoveAnimation(circleText5, 4, 50f + 100f) {
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    startRotationAndMoveAnimation(circleText6, 5, 50f + 200f) {
                                                        stopMixSound()
                                                        resetHandButtons()
                                                        enableButtons()
                                                        isRunning = false
                                                    }
                                                }, 1000)
                                            }
                                        }, 1000)
                                    }
                                }, 1000)
                            }
                        }, 1000)
                    }
                }, 1000)
            }
        }, 2000)
    }

    // 번호 애니메이션
    private fun animateNumberDisplay(textView: TextView) {
        val animator = ObjectAnimator.ofFloat(textView, "translationX", 1000f, 0f)  // 오른쪽에서 왼쪽으로 이동
        animator.duration = 1000 // 1초 동안 애니메이션
        animator.start()
    }

    private fun updateCircle() {
        // 원의 크기, 숫자 등을 변경할 수 있도록 설정
        val layoutParams1 = circleText1.layoutParams
        val layoutParams2 = circleText2.layoutParams
        val layoutParams3 = circleText3.layoutParams
        val layoutParams4 = circleText4.layoutParams
        val layoutParams5 = circleText5.layoutParams
        val layoutParams6 = circleText6.layoutParams
        layoutParams1.width = circleSize
        layoutParams1.height = circleSize
        layoutParams2.width = circleSize
        layoutParams2.height = circleSize
        layoutParams3.width = circleSize
        layoutParams3.height = circleSize
        layoutParams4.width = circleSize
        layoutParams4.height = circleSize
        layoutParams5.width = circleSize
        layoutParams5.height = circleSize
        layoutParams6.width = circleSize
        layoutParams6.height = circleSize
        circleText1.layoutParams = layoutParams1
        circleText2.layoutParams = layoutParams2
        circleText3.layoutParams = layoutParams1
        circleText4.layoutParams = layoutParams2
        circleText5.layoutParams = layoutParams1
        circleText6.layoutParams = layoutParams2

        // 숫자 업데이트
        circleText1.text = "1"
        circleText2.text = "2"
        circleText3.text = "3"
        circleText4.text = "4"
        circleText5.text = "5"
        circleText6.text = "6"
    }
    private fun startRotationAndMoveAnimation(textView: TextView,
                                              idx_ball: Int,
                                              endX: Float,
                                              onAnimationEnd: (() -> Unit)? = null) {
        // 원을 보이게 하는 애니메이션 (visibility를 VISIBLE로 설정)
        val fadeIn = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f)
        fadeIn.duration = 50 // 1초 동안 나타나도록 설정

        // 회전 애니메이션 (반시계방향으로 회전)
        val rotation = ObjectAnimator.ofFloat(textView, "rotation",
            (-360 / Math.PI * idx_ball).toFloat(),
            - 360f * rotationCount)
        //rotation.duration = ((rotationSpeed * rotationCount) + (5 - idx_ball) / 5 * rotationSpeed * 360 / Math.PI).toLong() // 회전 속도
        rotation.duration = ((1000 - 100 * idx_ball) * 3).toLong()

        // X축 이동 애니메이션 (오른쪽에서 왼쪽으로 이동)
        val moveX = ObjectAnimator.ofFloat(textView, "translationX", startX, endX)
        //moveX.duration = moveSpeed.toLong() // 이동 속도
        moveX.duration = ((1000 - 100 * idx_ball) * 3).toLong()

        // 애니메이션들을 동시에 실행
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(fadeIn, rotation, moveX) // fadeIn, 회전, 이동 애니메이션을 동시에 실행

        // 원이 보이도록 설정 후 애니메이션 시작
        textView.visibility = View.VISIBLE

        // 구르는 소리 반복 재생
        rollSoundStreamId = soundPool.play(rollSoundId, 0.3f, 0.3f, 0, -1, 1f)

        // 애니메이션 종료 후 "딱" 소리 재생
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                // 회전이 끝나면 부딪히는 소리 재생
                //soundPool.play(collisionSoundId, 1f, 1f, 0, 0, 1f) // "딱" 소리 재생
                soundPool.stop(rollSoundStreamId) // 구르는 소리 중지


                onAnimationEnd?.invoke()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })

        animatorSet.start() // 애니메이션 시작
    }

    // CSV 파일에서 가중치를 불러오는 함수
    private fun readWeightsFromCsv(): List<Double> {
        val file = File(requireContext().filesDir, "weights.csv")
        val weights = if (file.exists()) {
            val tempWeights = mutableListOf<Double>()
            val reader = BufferedReader(InputStreamReader(FileInputStream(file)))
            reader.forEachLine {
                val value = it.toDoubleOrNull() ?: 50.0  // 잘못된 값은 50.0으로 처리
                tempWeights.add(value)
            }
            reader.close()
            tempWeights
        } else {
            List(45) { 50.0 }  // 파일이 없으면 기본값 50으로 초기화
        }

        // 각 공의 가중치를 로그에 기록
        println("불러온 가중치:")
        weights.forEachIndexed { index, weight ->
            println("번호 ${index + 1}: 가중치 $weight")
        }
        return weights
    }



    // 애니메이션 함수
    private fun animateHandButton(selectedButton: ImageButton, otherButton: ImageButton, moveToRight: Boolean, onEnd: () -> Unit) {

        // 다른 버튼을 페이드 아웃
        val fadeOut = ObjectAnimator.ofFloat(otherButton, "alpha", 1f, 0f).apply {
            duration = 500
        }

        // 선택된 버튼을 이동하여 Start 버튼 위로 위치
        val moveX = if (moveToRight) {
            ObjectAnimator.ofFloat(selectedButton, "translationX", 120f)
        } else {
            ObjectAnimator.ofFloat(selectedButton, "translationX", -120f)
        }.apply {
            duration = 500
        }

        // 선택된 버튼을 페이드 인
        //val fadeIn = ObjectAnimator.ofFloat(selectedButton, "alpha", 0f, 1f).apply {
        //    duration = 500
        //}

        // 애니메이션을 순차적으로 실행
        AnimatorSet().apply {
            playTogether(fadeOut, moveX)
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    onEnd() // Start 버튼 누르는 동작 수행
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            start()
        }
    }

    // Start 버튼 동작 후 버튼을 원래 위치로 리셋하는 함수
    private fun resetHandButtons() {
        // 선택된 버튼을 페이드 아웃
        var fadeOutSelected = ObjectAnimator.ofFloat(righthandButton, "alpha", 1f, 0f).apply {
            duration = 200
        }
        if (lefthandButton.alpha == 1f) {
            fadeOutSelected = ObjectAnimator.ofFloat(lefthandButton, "alpha", 1f, 0f).apply {
                duration = 200
            }
        }

        // 모든 버튼을 원래 위치로 이동
        val resetLeft = ObjectAnimator.ofFloat(lefthandButton, "translationX", lefthandButton.x, 0f).apply {
            duration = 0
        }
        val resetRight = ObjectAnimator.ofFloat(righthandButton, "translationX", righthandButton.x, 0f).apply {
            duration = 0
        }

        // 모든 버튼을 페이드 인
        val fadeInLeft = ObjectAnimator.ofFloat(lefthandButton, "alpha", 0f, 1f).apply {
            duration = 500
        }
        val fadeInRight = ObjectAnimator.ofFloat(righthandButton, "alpha", 0f, 1f).apply {
            duration = 500
        }

        AnimatorSet().apply {
            play(fadeOutSelected).before(AnimatorSet().apply {
                playTogether(resetLeft, resetRight, fadeInLeft, fadeInRight)
            })
            start()
        }
    }

    private fun playMixSound(startOffset: Int){
        mixingBallMediaPlayer.seekTo(startOffset)
        mixingBallMediaPlayer.setVolume(0.25f, 0.25f)
        mixingBallMediaPlayer.start()
    }
    private fun stopMixSound() {
        mixingBallMediaPlayer.let {
            if (it.isPlaying) {
                it.stop()
                it.prepare() // stop 후 다시 사용하기 위해 prepare 호출
            }
        }
    }

    fun disableButtons() {
        lefthandButton.isEnabled = false
        righthandButton.isEnabled = false
    }

    fun enableButtons() {
        lefthandButton.isEnabled = true
        righthandButton.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        stopMixSound() // 사운드 중지
        soundPool.autoPause() // SoundPool 일시 중지
        isRunning = false // 동작 상태 초기화
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume() // SoundPool 다시 시작
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
        mixingBallMediaPlayer.release()
        buttonMediaPlayer.release()
        isRunning = false // 상태 초기화
    }
}
