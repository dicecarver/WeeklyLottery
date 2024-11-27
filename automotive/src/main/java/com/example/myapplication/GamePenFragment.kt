package com.example.myapplication
import android.animation.Animator
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlin.random.Random
import android.os.Handler
import android.os.Looper
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import android.animation.ObjectAnimator
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

class GamePenFragment : Fragment() {

    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var paperLayout_lotterypaper: LinearLayout
    private lateinit var gridLayout_lotterypaper: GridLayout
    private lateinit var headImg_lotterypaper: ImageView
    private lateinit var dashline_lotterypaper: View
    private lateinit var dashline_lotterypaper_2: View

    private val handler = Handler(Looper.getMainLooper())
    private val selectedTextViews = mutableListOf<TextView>() // 선택된 TextView 리스트

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_game_pen, container, false)
        resetTextViewColors(rootView, "Black")

        paperLayout_lotterypaper = rootView.findViewById(R.id.paperLayout_lotterypaper)
        headImg_lotterypaper = rootView.findViewById(R.id.headImg_lotterypaper)
        dashline_lotterypaper = rootView.findViewById(R.id.headDashline_lotterypaper)
        gridLayout_lotterypaper = rootView.findViewById(R.id.gridLayout_lotterypaper)
        dashline_lotterypaper_2 = rootView.findViewById(R.id.headDashline_lotterypaper_2)

        setInitialAlpha(headImg_lotterypaper, 0.2f)
        setInitialAlpha(dashline_lotterypaper, 0.2f)
        setInitialAlpha(gridLayout_lotterypaper, 0.2f)
        setInitialAlpha(dashline_lotterypaper_2, 0.2f)

        val startSlotButton = rootView.findViewById<View>(R.id.startSlotButton)
        val startSlotButton_gold = rootView.findViewById<View>(R.id.startSlotButton_gold)
        val startSlotButton_red = rootView.findViewById<View>(R.id.startSlotButton_red)
        val startSlotButton_green = rootView.findViewById<View>(R.id.startSlotButton_green)

        startSlotButton.setOnClickListener {
            setInitialAlpha(headImg_lotterypaper, 1.0f)
            setInitialAlpha(dashline_lotterypaper, 1.0f)
            setInitialAlpha(gridLayout_lotterypaper, 1.0f)
            setInitialAlpha(dashline_lotterypaper_2, 1.0f)
            // 초기 상태로 복구
            resetTextViewColors(rootView, "Black")
            // 새로운 6개의 TextView 선택
            val selectedSets = selectRandomTextViews(rootView, "Black")
            // 순차적으로 애니메이션 실행
            revealSquentiallyWithDelay(selectedSets)

        }
        startSlotButton_red.setOnClickListener {
            setInitialAlpha(headImg_lotterypaper, 1.0f)
            setInitialAlpha(dashline_lotterypaper, 1.0f)
            setInitialAlpha(gridLayout_lotterypaper, 1.0f)
            setInitialAlpha(dashline_lotterypaper_2, 1.0f)
            resetTextViewColors(rootView, "Red")
            val selectedSets = selectRandomTextViews(rootView, "Red")
            revealSquentiallyWithDelay(selectedSets)
        }
        startSlotButton_green.setOnClickListener {
            setInitialAlpha(headImg_lotterypaper, 1.0f)
            setInitialAlpha(dashline_lotterypaper, 1.0f)
            setInitialAlpha(gridLayout_lotterypaper, 1.0f)
            setInitialAlpha(dashline_lotterypaper_2, 1.0f)
            resetTextViewColors(rootView, "Green")
            val selectedSets = selectRandomTextViews(rootView, "Green")
            revealSquentiallyWithDelay(selectedSets)
        }
        startSlotButton_gold.setOnClickListener {
            setInitialAlpha(headImg_lotterypaper, 1.0f)
            setInitialAlpha(dashline_lotterypaper, 1.0f)
            setInitialAlpha(gridLayout_lotterypaper, 1.0f)
            setInitialAlpha(dashline_lotterypaper_2, 1.0f)
            resetTextViewColors(rootView, "Gold")
            val selectedSets = selectRandomTextViews(rootView, "Gold")
            revealSquentiallyWithDelay(selectedSets)
        }

        return rootView
    }

    companion object {
        fun newInstance(): GamePenFragment {
            return GamePenFragment()
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

    // 모든 View의 투명도를 서서히 높이는 애니메이션
    private fun fadeInViews(view: View, duration: Long = 1000) {
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                fadeInViews(view.getChildAt(i), duration)
            }
        } else {
            ObjectAnimator.ofFloat(view, "alpha", view.alpha, 1.0f).apply {
                this.duration = duration
                start()
            }
        }
    }

    private fun playSound(startOffset: Int){
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.marker_sound)
        mediaPlayer.seekTo(startOffset)
        mediaPlayer.start()
    }
    private fun stopSound() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
    }

    // 모든 TextView를 초기 색상으로 복구
    private fun resetTextViewColors(rootView: View, text: String) {
        val allTextViewIds = (1..45).map { index ->
            rootView.findViewById<TextView>(
                resources.getIdentifier(
                    "numberTextView$index",
                    "id",
                    requireContext().packageName
                )
            ).apply {
                this?.text = index.toString()
            }
        }
        val allTextViewIds_first = listOf("first", "firstRed", "firstGold", "firstGreen").flatMap { suffix ->
            (1..45).map { index ->
                rootView.findViewById<TextView>(
                    resources.getIdentifier(
                        "numberTextView${index}_$suffix",
                        "id",
                        requireContext().packageName
                    )
                ).apply {
                    this?.text = index.toString()
                }
            }
        }
        val allTextViewIds_second = listOf("second", "secondRed", "secondGold", "secondGreen").flatMap { suffix ->
            (1..45).map { index ->
                rootView.findViewById<TextView>(
                    resources.getIdentifier(
                        "numberTextView${index}_$suffix",
                        "id",
                        requireContext().packageName
                    )
                ).apply {
                    this?.text = index.toString()
                }
            }
        }

        for (textView_origin in allTextViewIds) {
            textView_origin?.visibility = View.VISIBLE
        }
        for (textView_first in allTextViewIds_first) {
            textView_first?.visibility = View.INVISIBLE
        }
        for (textView_second in allTextViewIds_second) {
            textView_second?.visibility = View.INVISIBLE
        }
    }

    // TextView를 랜덤으로 6개 선택
    private fun selectRandomTextViews(rootView: View, text: String): List<Triple<TextView, TextView, TextView>> {
        val suffix = when (text) {
            "Red" -> "Red"
            "Gold" -> "Gold"
            "Green" -> "Green"
            else -> ""
        }

        var numberWeights = IntArray(45) { 50 } // 기본값
        val balls = (1..45).toMutableList() // 1부터 45까지의 공 리스트
        val selectedNumbers = mutableListOf<Int>()
        val initialWeights = readWeightsFromCsv() // CSV 파일에서 가중치 불러오기 (없으면 기본값 50 설정)
        var weights = initialWeights.toMutableList() // 가중치 리스트 복사

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

        val selectedSets = selectedNumbers.map { number ->
            val textViewMain = rootView.findViewById<TextView>(
                resources.getIdentifier("numberTextView$number", "id", requireContext().packageName)
            )
            val textViewFirst = rootView.findViewById<TextView>(
                resources.getIdentifier("numberTextView${number}_first$suffix", "id", requireContext().packageName)
            )
            val textViewSecond = rootView.findViewById<TextView>(
                resources.getIdentifier("numberTextView${number}_second$suffix", "id", requireContext().packageName)
            )
            Triple(textViewMain, textViewFirst, textViewSecond)
        }

        return selectedSets
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

    private fun revealSquentiallyWithDelay(selectedSets: List<Triple<TextView, TextView, TextView>>){
        var index = 0

        fun animateNextSet() {
            if (index < selectedSets.size) {
                val (_, firstTextView, secondTextView) = selectedSets[index]

                revealViewFromTop(firstTextView) {
                    revealViewFromBottom(secondTextView) {
                        index++
                        handler.postDelayed({ animateNextSet()}, 500)
                    }
                }
            }
        }

        animateNextSet()
    }


    private fun revealViewFromTop(view: View, onAnimationEnd: () -> Unit) {
        view.visibility = View.VISIBLE
        val centerX = view.width / 2
        val centerY = (view.height * 0.1).toInt()
        val startRadius = (view.width / 2).toFloat()
        val finalRadius = Math.hypot(centerX.toDouble(), view.height.toDouble()).toFloat()

        val revealAnimation = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, finalRadius)
        revealAnimation.duration = 500
        revealAnimation.interpolator = AccelerateDecelerateInterpolator()
        revealAnimation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                playSound(0)
            }
            override fun onAnimationEnd(animator: Animator) {
                stopSound()
                onAnimationEnd()
            }
            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })
        revealAnimation.start()
    }
    private fun revealViewFromBottom(view: View, onAnimationEnd: () -> Unit) {
        view.visibility = View.VISIBLE
        val centerX = view.width / 2
        val centerY = (view.height * 0.9).toInt()
        val startRadius = (view.width / 2).toFloat()
        val finalRadius = Math.hypot(centerX.toDouble(), view.height.toDouble()).toFloat()

        val revealAnimation = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, finalRadius)
        revealAnimation.duration = 500
        revealAnimation.interpolator = AccelerateDecelerateInterpolator()
        revealAnimation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                playSound(500)
            }
            override fun onAnimationEnd(animator: Animator) {
                stopSound()
                onAnimationEnd()
            }
            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })
        revealAnimation.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized){
            mediaPlayer.release()
        }
    }
}