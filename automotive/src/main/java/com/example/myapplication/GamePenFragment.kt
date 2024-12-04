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
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader

class GamePenFragment : Fragment() {

    private var isMediaPlayerReleased = false

    private var flag_playing = false
    private var isAnimationPlaying = false

    private lateinit var markermediaPlayer: MediaPlayer

    private lateinit var paperLayout_lotterypaper: LinearLayout
    private lateinit var gridLayout_lotterypaper: GridLayout
    private lateinit var headImg_lotterypaper: ImageView
    private lateinit var dashline_lotterypaper: View
    private lateinit var dashline_lotterypaper_2: View

    private lateinit var popupButton: AppCompatButton

    private val handler = Handler(Looper.getMainLooper())
    private val selectedTextViews = mutableListOf<TextView>() // 선택된 TextView 리스트

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_game_pen, container, false)
        resetTextViewColors(rootView, "Black")

        flag_playing = false

        markermediaPlayer = MediaPlayer.create(requireContext(), R.raw.marker_sound)

        paperLayout_lotterypaper = rootView.findViewById(R.id.paperLayout_lotterypaper)
        headImg_lotterypaper = rootView.findViewById(R.id.headImg_lotterypaper)
        dashline_lotterypaper = rootView.findViewById(R.id.headDashline_lotterypaper)
        gridLayout_lotterypaper = rootView.findViewById(R.id.gridLayout_lotterypaper)
        dashline_lotterypaper_2 = rootView.findViewById(R.id.headDashline_lotterypaper_2)

        val startSlotButton = rootView.findViewById<View>(R.id.startSlotButton)
        val startSlotButton_gold = rootView.findViewById<View>(R.id.startSlotButton_gold)
        val startSlotButton_red = rootView.findViewById<View>(R.id.startSlotButton_red)
        val startSlotButton_green = rootView.findViewById<View>(R.id.startSlotButton_green)

        setInitialAlpha(paperLayout_lotterypaper, 0.05f)
        setInitialAlpha(headImg_lotterypaper, 0.2f)
        setInitialAlpha(dashline_lotterypaper, 0.2f)
        setInitialAlpha(gridLayout_lotterypaper, 0.2f)
        setInitialAlpha(dashline_lotterypaper_2, 0.2f)

        setInitialAlpha(startSlotButton, 1.0f)
        setInitialAlpha(startSlotButton_gold, 1.0f)
        setInitialAlpha(startSlotButton_red, 1.0f)
        setInitialAlpha(startSlotButton_green, 1.0f)

        popupButton = rootView.findViewById(R.id.popupButton)


        startSlotButton.setOnClickListener {
            if (flag_playing) {
                Toast.makeText(requireContext(), "이미 로또 추첨이 진행중입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            flag_playing = true
            popupButton.visibility = View.GONE
            setInitialAlpha(paperLayout_lotterypaper, 1.0f)
            setInitialAlpha(headImg_lotterypaper, 1.0f)
            setInitialAlpha(dashline_lotterypaper, 1.0f)
            setInitialAlpha(gridLayout_lotterypaper, 1.0f)
            setInitialAlpha(dashline_lotterypaper_2, 1.0f)
            // 초기 상태로 복구
            resetTextViewColors(rootView, "Black")
            // 새로운 6개의 TextView 선택
            val selectedSets = selectRandomTextViews(rootView, "Black")
            // 순차적으로 애니메이션 실행
            revealSquentiallyWithDelay(selectedSets){flag_playing = false}

        }
        startSlotButton_red.setOnClickListener {
            if (flag_playing){
                Toast.makeText(requireContext(), "이미 로또 추첨이 진행중입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            flag_playing = true
            popupButton.visibility = View.GONE
            setInitialAlpha(paperLayout_lotterypaper, 1.0f)
            setInitialAlpha(headImg_lotterypaper, 1.0f)
            setInitialAlpha(dashline_lotterypaper, 1.0f)
            setInitialAlpha(gridLayout_lotterypaper, 1.0f)
            setInitialAlpha(dashline_lotterypaper_2, 1.0f)
            resetTextViewColors(rootView, "Red")
            val selectedSets = selectRandomTextViews(rootView, "Red")
            revealSquentiallyWithDelay(selectedSets){flag_playing = false}
        }
        startSlotButton_green.setOnClickListener {
            if (flag_playing){
                Toast.makeText(requireContext(), "이미 로또 추첨이 진행중입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            flag_playing = true
            popupButton.visibility = View.GONE
            setInitialAlpha(paperLayout_lotterypaper, 1.0f)
            setInitialAlpha(headImg_lotterypaper, 1.0f)
            setInitialAlpha(dashline_lotterypaper, 1.0f)
            setInitialAlpha(gridLayout_lotterypaper, 1.0f)
            setInitialAlpha(dashline_lotterypaper_2, 1.0f)
            resetTextViewColors(rootView, "Green")
            val selectedSets = selectRandomTextViews(rootView, "Green")
            revealSquentiallyWithDelay(selectedSets){flag_playing = false}
        }
        startSlotButton_gold.setOnClickListener {
            if (flag_playing){
                Toast.makeText(requireContext(), "이미 로또 추첨이 진행중입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            flag_playing = true
            popupButton.visibility = View.GONE
            setInitialAlpha(paperLayout_lotterypaper, 1.0f)
            setInitialAlpha(headImg_lotterypaper, 1.0f)
            setInitialAlpha(dashline_lotterypaper, 1.0f)
            setInitialAlpha(gridLayout_lotterypaper, 1.0f)
            setInitialAlpha(dashline_lotterypaper_2, 1.0f)
            resetTextViewColors(rootView, "Gold")
            val selectedSets = selectRandomTextViews(rootView, "Gold")
            revealSquentiallyWithDelay(selectedSets){flag_playing = false}
        }

        // AppCompatButton 참조
        val popupButton: AppCompatButton = rootView.findViewById(R.id.popupButton)

        // 전체 텍스트
        val text = "마킹할 펜을 선택해주세요"

        // 특정 부분만 굵게 처리 (예: "펜" 부분)
        val spannable = SpannableString(text)
        val boldStart = text.indexOf("펜") // "펜" 시작 인덱스
        val boldEnd = boldStart + "펜".length // "펜" 끝 인덱스
        spannable.setSpan(
            StyleSpan(Typeface.BOLD), // 굵게 처리
            boldStart,
            boldEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // 글자 크기 크게 (1.5배)
        spannable.setSpan(
            RelativeSizeSpan(1.5f),
            boldStart,
            boldEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // 버튼에 Spannable 텍스트 설정
        popupButton.text = spannable

        popupButton.setOnClickListener {
            popupButton.visibility = View.GONE
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
    private fun restartMediaPlayer(startOffset: Int) {
        try {
            markermediaPlayer = MediaPlayer.create(requireContext(), R.raw.marker_sound).apply {
                seekTo(startOffset)
                start()
            }
            Log.i("GamePenFragment", "MediaPlayer restarted successfully")
        } catch (e: Exception) {
            Log.e("GamePenFragment", "Failed to restart MediaPlayer: ${e.message}")
        }
    }
    private fun playSoundSafely(mediaPlayer: MediaPlayer, startOffset: Int) {
        try {
            if (::markermediaPlayer.isInitialized) {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                mediaPlayer.reset()
                mediaPlayer.release()
            }
        } catch (e: IllegalStateException) {
            Log.e("GamePenFragment", "MediaPlayer IllegalStateException: ${e.message}")
        } catch (e: Exception) {
            Log.e("GamePenFragment", "Unexpected error: ${e.message}")
        }

        // MediaPlayer 새로 생성
        markermediaPlayer = MediaPlayer.create(requireContext(), R.raw.marker_sound).apply {
            seekTo(startOffset)
            start()
        }
    }
    private fun playSound(startOffset: Int){
        //mediaPlayer = MediaPlayer.create(requireContext(), R.raw.marker_sound)
        //mediaPlayer.seekTo(startOffset)
        //mediaPlayer.start()
        playSoundSafely(markermediaPlayer, startOffset)

        /*try {
            // 기존 MediaPlayer가 존재하면 정리
            if (::mediaPlayer.isInitialized) {
                mediaPlayer.reset() // MediaPlayer 초기화
                mediaPlayer.release()
            }

            // 새 MediaPlayer 생성
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.marker_sound).apply {
                seekTo(startOffset) // 시작 위치 설정
                start() // 재생
            }

        } catch (e: IllegalStateException) {
            Log.e("GamePenFragment", "IllegalStateException: ${e.message}")
            restartMediaPlayer(startOffset) // 문제 발생 시 MediaPlayer 재시작

        } catch (e: IOException) {
            Log.e("GamePenFragment", "IOException: ${e.message}")
            restartMediaPlayer(startOffset) // IOException 발생 시 재시작

        } catch (e: Exception) {
            Log.e("GamePenFragment", "Unexpected error: ${e.message}")
            restartMediaPlayer(startOffset) // 기타 예외 처리
        }*/
    }

    private fun stopSound() {
        try {
            if (::markermediaPlayer.isInitialized && markermediaPlayer.isPlaying) {
                markermediaPlayer.stop()
            }
            markermediaPlayer.release()
        }catch (e: IllegalStateException) {
            Log.e("GamePenFragment", "MediaPlayer IllegalStateException: ${e.message}")
        }
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

    private fun revealSquentiallyWithDelay(
        selectedSets: List<Triple<TextView, TextView, TextView>>,
        onComplete: () -> Unit // 애니메이션 완료 후 실행될 콜백
    ) {
        var index = 0

        fun animateNextSet() {
            if (index < selectedSets.size) {
                val (_, firstTextView, secondTextView) = selectedSets[index]

                // 첫 번째 뷰 애니메이션
                revealViewFromTop(firstTextView) {
                    // 두 번째 뷰 애니메이션
                    revealViewFromBottom(secondTextView) {
                        index++
                        // 다음 세트를 일정 시간 후 실행
                        handler.postDelayed({ animateNextSet() }, 500)
                    }
                }
            } else {
                // 모든 애니메이션 완료 시 콜백 호출
                onComplete()
            }
        }

        animateNextSet()
    }


    private fun revealViewFromTop(view: View, onAnimationEnd: () -> Unit) {
        view.visibility = View.VISIBLE

        if (!view.isAttachedToWindow) {
            Log.e("GamePenFragment", "View is detached, skipping animation")
            return
        }

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

        if (!view.isAttachedToWindow) {
            Log.e("GamePenFragment", "View is detached, skipping animation")
            return
        }

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

    override fun onResume() {
        super.onResume()

        // 초기화 작업
        flag_playing = false
        //setInitialAlpha(headImg_lotterypaper, 1.0f)
        //setInitialAlpha(dashline_lotterypaper, 1.0f)
        //setInitialAlpha(gridLayout_lotterypaper, 1.0f)
        //setInitialAlpha(dashline_lotterypaper_2, 1.0f)
        resetTextViewColors(requireView(), "Black")
    }

    override fun onDestroy() {
        super.onDestroy()
        flag_playing = false
        handler.removeCallbacksAndMessages(null) // 모든 지연 작업 취소
        try {
            if (::markermediaPlayer.isInitialized) {
                markermediaPlayer.stop()
                markermediaPlayer.release()
            }
        } catch (e: IllegalStateException) {
            Log.e("GamePenFragment", "Error releasing MediaPlayer: ${e.message}")
        }
    }

}