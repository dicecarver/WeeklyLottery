package com.example.myapplication

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import java.io.BufferedReader
import java.io.InputStreamReader

import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter



class HistoryNumbersFragment : Fragment() {

    private lateinit var chartTitle: TextView
    private lateinit var barChart: BarChart
    private lateinit var rangeSeekBar: SeekBar
    private lateinit var rangeLabel: TextView
    private lateinit var bonusSwitch: Switch
    private lateinit var last_round_txt: TextView

    private var rangeOptions: List<String> = emptyList()
    private var rangeDescriptions: List<String> = emptyList()
    private var last_round_val: String = "##"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_history_numbers, container, false)
        chartTitle = rootView.findViewById(R.id.chartTitle)
        barChart = rootView.findViewById(R.id.barChart)
        rangeSeekBar = rootView.findViewById(R.id.rangeSeekBar)
        rangeLabel = rootView.findViewById(R.id.rangeLabel)
        bonusSwitch = rootView.findViewById(R.id.bonusSwitch)
        last_round_txt = rootView.findViewById(R.id.last_round_txt)

        loadRangeData()  // 메타데이터 파일에서 rangeOptions와 rangeDescriptions 불러오기

        //rangeSeekBar.progress = rangeSeekBar.max
        rangeSeekBar.progress = (rangeSeekBar.max * 0.5).toInt()
        rangeLabel.text = "(${rangeDescriptions[rangeSeekBar.progress]})"
        //rangeSeekBar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.main_gold))
        setupSeekBar()
        setupSwitch()
        updateChart()

        last_round_txt.text = "${last_round_val}회"

        return rootView
    }
    companion object {
        fun newInstance(): HistoryNumbersFragment {
            return HistoryNumbersFragment()
        }
    }
    private fun loadRangeData() {
        val inputStream = requireContext().assets.open("lottery_ball_count_metadata.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val lines = reader.readLines()

        val options = mutableListOf<String>()
        val descriptions = mutableListOf<String>()
        val start_round = mutableListOf<String>()

        // 첫 번째 줄(헤더)은 건너뜁니다
        for (i in 1 until lines.size) {
            val tokens = lines[i].split(",")
            // 보너스 포함이 아닌 항목만 추가
            if (tokens.size >= 2 && !tokens[0].contains("_with_bonus")) {
                options.add(tokens[0]) // 옵션 이름 (예: "All", "Recent_500", ...)
                descriptions.add(tokens[1]) // 설명 (예: "1~1144회차 (전회차)", "645~1144회차 (최근 500회)", ...)
                start_round.add(tokens[2])
            }
        }
        reader.close()
        last_round_val = start_round[0]
        rangeOptions = options.toList()
        rangeDescriptions = descriptions.toList()
        rangeSeekBar.max = rangeOptions.size - 1  // SeekBar의 최대값 설정
    }

    private fun updateChart() {
        val selectedRangeIndex = rangeSeekBar.progress
        val columnName = if (bonusSwitch.isChecked) {
            "${rangeOptions[selectedRangeIndex]}_with_bonus"
        } else {
            rangeOptions[selectedRangeIndex]
        }

        val entries = loadChartDataFromCSV(columnName)

        if (entries.isEmpty()) {
            println("Column $columnName 에 대한 데이터를 찾을 수 없습니다.")
            return
        }

        // 막대 데이터의 y 값을 기준으로 내림차순 정렬하여 상위 세 값을 구합니다.
        val sortedYValues = entries.map { it.y }.distinct().sortedDescending()
        val top1Value = sortedYValues.getOrNull(0) ?: 0f
        val top2Value = sortedYValues.getOrNull(1) ?: 0f
        val top3Value = sortedYValues.getOrNull(2) ?: 0f
        val sortedYValues_reverse = entries.map { it.y }.distinct().sorted()
        val bottom1Value = sortedYValues_reverse.getOrNull(0) ?: 0f
        val bottom2Value = sortedYValues_reverse.getOrNull(1) ?: 0f
        val bottom3Value = sortedYValues_reverse.getOrNull(2) ?: 0f

        val dataSet = BarDataSet(entries, "번호별 출현 횟수 (${if (bonusSwitch.isChecked) "보너스 포함" else "보너스 미포함"})")
        dataSet.valueTextSize = 10f

        // 색상 설정
        val goldColor = ContextCompat.getColor(requireContext(), R.color.chart_green)    // 금색: 가장 큰 값
        val silverColor = ContextCompat.getColor(requireContext(), R.color.chart_semi_green) // 은색: 두 번째로 큰 값
        val bronzeColor = ContextCompat.getColor(requireContext(), R.color.bronze) // 동색: 세 번째로 큰 값
        val defaultColor = ContextCompat.getColor(requireContext(), R.color.chart_gray) // 나머지 막대 색상: 회색
        val redColor = ContextCompat.getColor(requireContext(), R.color.chart_red)
        val semiredColor = ContextCompat.getColor(requireContext(), R.color.chart_semi_red) // 은색: 두 번째로 큰 값

        // 각 막대 색상 설정
        dataSet.colors = entries.map { entry ->
            when (entry.y) {
                top1Value -> goldColor
                top2Value -> silverColor
                bottom1Value -> redColor
                bottom2Value -> semiredColor
                else -> defaultColor
            }
        }

        dataSet.valueTextColor = android.graphics.Color.WHITE

        val barData = BarData(dataSet)
        barData.barWidth = 0.8f

        barChart.data = barData
        barChart.setFitBars(true)
        barChart.invalidate()
        barChart.animateY(800)

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(false)
        barChart.setScaleEnabled(true) // 차트 스케일 비활성화

        //xAxis.granularity = 1f
        //xAxis.isGranularityEnabled = true
        //xAxis.axisMinimum = 1f  // x축의 시작 값 설정
        //xAxis.axisMaximum = 45f  // x축의 끝 값 설정
        //xAxis.textSize = 15f     // 텍스트 크기 설정
        //xAxis.textColor = ContextCompat.getColor(this, R.color.ball_white)  // 텍스트 색상을 흰색으로 설정
        //xAxis.typeface = android.graphics.Typeface.DEFAULT_BOLD // 텍스트 진하게 설정

        barChart.setExtraOffsets(10f, 10f, 10f, 30f)

        val yAxisLeft = barChart.axisLeft
        yAxisLeft.setDrawGridLines(false)
        yAxisLeft.textColor = ContextCompat.getColor(requireContext(), R.color.ball_white)
        yAxisLeft.axisMinimum = 0f

        val yAxisRight = barChart.axisRight
        yAxisRight.isEnabled = false

        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() // 소수점 제거
            }
        }

        barChart.description.isEnabled = false
        // 차트 배경 설정
        barChart.setDrawGridBackground(false)  // 그리드 배경 제거
        barChart.setBackgroundColor(android.graphics.Color.TRANSPARENT)  // 배경을 투명하게 설정
        //barChart.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
        barChart.legend.isEnabled = false
    }

    private fun setupSeekBar() {
        rangeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                rangeLabel.text = "(${rangeDescriptions[progress]})"
                updateChart()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupSwitch() {
        bonusSwitch.setOnCheckedChangeListener { _, _ ->
            updateChart()
        }
    }

    private fun loadChartDataFromCSV(columnName: String): List<BarEntry> {
        val entries = mutableListOf<BarEntry>()
        val inputStream = requireContext().assets.open("lottery_ball_count.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))

        val lines = reader.readLines()
        val headers = lines[0].split(",")
        val columnIndex = headers.indexOf(columnName)

        if (columnIndex == -1) {
            println("Column $columnName not found in lottery_ball_count.csv.")
            reader.close()
            return entries
        }

        for (i in 1..45) {
            val line = lines[i]
            val tokens = line.split(",")
            if (tokens.size > columnIndex) {
                val value = tokens[columnIndex].toFloatOrNull() ?: 0f
                entries.add(BarEntry(i.toFloat(), value))
            }
        }
        reader.close()
        return entries
    }

}