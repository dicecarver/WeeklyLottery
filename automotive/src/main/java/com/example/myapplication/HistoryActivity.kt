package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import java.io.BufferedReader
import java.io.InputStreamReader

class HistoryActivity : AppCompatActivity() {
    private lateinit var chartTitle: TextView
    private lateinit var barChart: BarChart
    private lateinit var rangeSeekBar: SeekBar
    private lateinit var rangeLabel: TextView
    private lateinit var bonusSwitch: Switch
    private lateinit var dateRecyclerView: RecyclerView
    private lateinit var dateButton: TextView
    private lateinit var numberButton: TextView
    private lateinit var dateLayout: LinearLayout
    private lateinit var numberLayout: LinearLayout
    private lateinit var bonusAndRangeOptions: LinearLayout
    private var rangeOptions: List<String> = emptyList()
    private var rangeDescriptions: List<String> = emptyList()
    private lateinit var lottoResults: List<LottoResult>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // UI 요소 초기화
        chartTitle = findViewById(R.id.chartTitle)
        barChart = findViewById(R.id.barChart)
        rangeSeekBar = findViewById(R.id.rangeSeekBar)
        rangeLabel = findViewById(R.id.rangeLabel)
        bonusSwitch = findViewById(R.id.bonusSwitch)
        dateRecyclerView = findViewById(R.id.dateRecyclerView)
        dateButton = findViewById(R.id.dateButton)
        numberButton = findViewById(R.id.numberButton)
        dateLayout = findViewById(R.id.dateLayout)
        numberLayout = findViewById(R.id.numberLayout)
        bonusAndRangeOptions = findViewById(R.id.bonusAndRangeOptions)

        loadRangeData()  // 메타데이터 파일에서 rangeOptions와 rangeDescriptions 불러오기
        lottoResults = parseDateData()
        dateRecyclerView.layoutManager = LinearLayoutManager(this)
        dateRecyclerView.adapter = LottoResultAdapter(lottoResults)

        setupSeekBar()
        setupSwitch()
        setupButtons()
        updateToggleState(isDateSelected = true)
        showDateBasedStatistics()
    }

    private fun loadRangeData() {
        val inputStream = assets.open("lottery_ball_count_metadata.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val lines = reader.readLines()

        val options = mutableListOf<String>()
        val descriptions = mutableListOf<String>()

        // 첫 번째 줄(헤더)은 건너뜁니다
        for (i in 1 until lines.size) {
            val tokens = lines[i].split(",")
            // 보너스 포함이 아닌 항목만 추가
            if (tokens.size >= 2 && !tokens[0].contains("_with_bonus")) {
                options.add(tokens[0]) // 옵션 이름 (예: "All", "Recent_500", ...)
                descriptions.add(tokens[1]) // 설명 (예: "1~1144회차 (전회차)", "645~1144회차 (최근 500회)", ...)
            }
        }
        reader.close()

        rangeOptions = options.toList()
        rangeDescriptions = descriptions.toList()
        rangeSeekBar.max = rangeOptions.size - 1  // SeekBar의 최대값 설정
    }


    private fun setupButtons() {
        dateButton.setOnClickListener {
            updateToggleState(isDateSelected = true)
            showDateBasedStatistics()
        }

        numberButton.setOnClickListener {
            updateToggleState(isDateSelected = false)
            showNumberBasedStatistics()
        }
    }

    private fun setupSeekBar() {
        rangeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                rangeLabel.text = "카운트 범위 설정: ${rangeDescriptions[progress]}"
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

    private fun updateToggleState(isDateSelected: Boolean) {
        dateButton.isSelected = isDateSelected
        numberButton.isSelected = !isDateSelected

        dateButton.setBackgroundColor(
            ContextCompat.getColor(this, if (isDateSelected) R.color.selectedBackgroundColor else R.color.unselectedBackgroundColor)
        )
        dateButton.setTextColor(ContextCompat.getColor(this, if (isDateSelected) R.color.selectedTextColor else R.color.unselectedTextColor))

        numberButton.setBackgroundColor(
            ContextCompat.getColor(this, if (!isDateSelected) R.color.selectedBackgroundColor else R.color.unselectedBackgroundColor)
        )
        numberButton.setTextColor(ContextCompat.getColor(this, if (!isDateSelected) R.color.selectedTextColor else R.color.unselectedTextColor))

        bonusAndRangeOptions.visibility = if (!isDateSelected) View.VISIBLE else View.GONE
    }

    private fun showDateBasedStatistics() {
        // 날짜별 레이아웃을 보이게 하고 번호별 레이아웃을 숨기기
        dateLayout.visibility = View.VISIBLE  // 날짜별 레이아웃을 보이게 설정
        numberLayout.visibility = View.GONE  // 번호별 레이아웃을 숨김
        barChart.visibility = View.GONE  // 번호별 차트 숨기기
        chartTitle.visibility = View.GONE  // 차트 제목 숨기기
        dateRecyclerView.visibility = View.VISIBLE  // 날짜별 리스트는 보이게 설정
        if (lottoResults.isEmpty()) {
            dateRecyclerView.visibility = View.GONE
        } else {
            dateRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun showNumberBasedStatistics() {
        // 번호별 레이아웃을 보이게 하고 날짜별 레이아웃을 숨기기
        dateLayout.visibility = View.GONE  // 날짜별 레이아웃 숨기기
        numberLayout.visibility = View.VISIBLE  // 번호별 레이아웃을 보이게 설정
        barChart.visibility = View.VISIBLE  // 번호별 차트를 보이게 설정
        chartTitle.visibility = View.VISIBLE  // 차트 제목 보이기
        dateRecyclerView.visibility = View.GONE  // 날짜별 리스트는 숨기기
        updateChart()  // 차트 업데이트
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

        val dataSet = BarDataSet(entries, "번호별 출현 횟수 (${if (bonusSwitch.isChecked) "보너스 포함" else "보너스 미포함"})")
        dataSet.valueTextSize = 10f

        // 색상 설정
        val goldColor = ContextCompat.getColor(this, R.color.gold)    // 금색: 가장 큰 값
        val silverColor = ContextCompat.getColor(this, R.color.silver) // 은색: 두 번째로 큰 값
        val bronzeColor = ContextCompat.getColor(this, R.color.bronze) // 동색: 세 번째로 큰 값
        val defaultColor = ContextCompat.getColor(this, R.color.gray) // 나머지 막대 색상: 회색

        // 각 막대 색상 설정
        dataSet.colors = entries.map { entry ->
            when (entry.y) {
                top1Value -> goldColor
                top2Value -> silverColor
                top3Value -> bronzeColor
                else -> defaultColor
            }
        }

        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        val barData = BarData(dataSet)
        barData.barWidth = 0.8f

        barChart.data = barData
        barChart.setFitBars(true)
        barChart.invalidate()
        barChart.animateY(800)

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.axisMinimum = 0.5f
        xAxis.axisMaximum = 45.5f
        xAxis.labelCount = 45
        xAxis.textColor = ContextCompat.getColor(this, android.R.color.black)

        val yAxisLeft = barChart.axisLeft
        yAxisLeft.setDrawGridLines(false)
        yAxisLeft.textColor = ContextCompat.getColor(this, android.R.color.black)
        yAxisLeft.axisMinimum = 0f

        val yAxisRight = barChart.axisRight
        yAxisRight.isEnabled = false

        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
        barChart.legend.isEnabled = false
    }


    private fun loadChartDataFromCSV(columnName: String): List<BarEntry> {
        val entries = mutableListOf<BarEntry>()
        val inputStream = assets.open("lottery_ball_count.csv")
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

    private fun parseDateData(): List<LottoResult> {
        val lottoResults = mutableListOf<LottoResult>()
        val inputStream = assets.open("win_data_all.csv") // 파일 이름을 win_data_all.csv로 설정
        val reader = BufferedReader(InputStreamReader(inputStream))
        val lines = reader.readLines()

        // 첫 번째 줄(헤더) 건너뛰기
        for (i in 1 until lines.size) {
            val line = lines[i]
            val tokens = line.split(",")

            // 필요한 데이터만 추출
            val round = tokens[0] // Round 번호
            val winningNumbers = tokens.subList(1, 7).map { it.toIntOrNull() ?: 0 } // 1~6번 번호
            val bonusNumber = tokens[7].toIntOrNull() ?: 0  // 보너스 번호
            val winnerCount = tokens[8].toIntOrNull() ?: 0  // 당첨자 수
            val prizeAmount = tokens[9].toLongOrNull() ?: 0  // 1인당 당첨금액

            // LottoResult 객체 생성
            val result = LottoResult(round, winningNumbers, bonusNumber, winnerCount, prizeAmount)
            lottoResults.add(result)
        }
        reader.close()

        return lottoResults
    }
}