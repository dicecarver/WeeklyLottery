package com.example.weeklylottery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.InputStreamReader

class HistoryDateFragment : Fragment() {

    private lateinit var dateRecyclerView: RecyclerView
    private lateinit var lottoResults: List<LottoResult>
    private lateinit var detailInfo_Prize_Head: TextView
    private lateinit var detailInfo_Prize: TextView
    private lateinit var detailInfo_Date: TextView
    private lateinit var detailInfo_Title: TextView
    private lateinit var detailInfo_Mode: TextView
    private lateinit var detailInfo_Region: TextView
    private lateinit var circleText1: TextView
    private lateinit var circleText2: TextView
    private lateinit var circleText3: TextView
    private lateinit var circleText4: TextView
    private lateinit var circleText5: TextView
    private lateinit var circleText6: TextView
    private lateinit var plusImg: TextView
    private lateinit var circleTextBonus: TextView
    private lateinit var ballLayout: FrameLayout
    private lateinit var guidepopup: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_history_date, container, false)

        dateRecyclerView = rootView.findViewById(R.id.dateRecyclerView)
        detailInfo_Prize_Head = rootView.findViewById(R.id.detailInfo_prize_head) // 상세 정보 TextView
        detailInfo_Prize = rootView.findViewById(R.id.detailInfo_Prize)
        detailInfo_Mode = rootView.findViewById(R.id.detailInfo_Mode)
        detailInfo_Region = rootView.findViewById(R.id.detailInfo_Region)
        detailInfo_Date = rootView.findViewById(R.id.detailInfo_Date)
        detailInfo_Title = rootView.findViewById(R.id.detailInfo_Title)
        circleText1 = rootView.findViewById(R.id.circleText1)
        circleText2 = rootView.findViewById(R.id.circleText2)
        circleText3 = rootView.findViewById(R.id.circleText3)
        circleText4 = rootView.findViewById(R.id.circleText4)
        circleText5 = rootView.findViewById(R.id.circleText5)
        circleText6 = rootView.findViewById(R.id.circleText6)
        plusImg = rootView.findViewById(R.id.plus_img)
        circleTextBonus = rootView.findViewById(R.id.circleTextBonus)
        ballLayout = rootView.findViewById(R.id.ballLayout)
        guidepopup = rootView.findViewById(R.id.guidepopup)

        lottoResults = parseDateData()

        dateRecyclerView.isVerticalScrollBarEnabled = true

        //val adapter = LottoResultAdapter(lottoResults){selectedResult -> showDetailInfo(selectedResult)}
        //dateRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        //dateRecyclerView.adapter = adapter
        dateRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        dateRecyclerView.adapter = LottoResultAdapter(lottoResults) { selectedResult ->
            updateDetailInfo(selectedResult)
        }

        ballLayout.alpha = 0.2f
        detailInfo_Prize_Head.alpha = 0.2f
        guidepopup.visibility = View.VISIBLE


        return rootView
    }

    companion object {
        fun newInstance(): HistoryDateFragment {
            return HistoryDateFragment()
        }
    }

    private fun updateDetailInfo(selectedResult: LottoResult) {
        ballLayout.alpha = 1.0f
        detailInfo_Prize_Head.alpha = 1.0f
        guidepopup.visibility = View.INVISIBLE

        // 1인당 당첨액 업데이트
        detailInfo_Prize.text = "${formatPrizeAmount(selectedResult.prizeAmount)}"
        detailInfo_Date.text = selectedResult.rounddate
        detailInfo_Title.text = "[${selectedResult.round}회차]"

        // 로또 당첨 번호 업데이트
        val numbers = selectedResult.winningNumbers
        updateCircleView(circleText1, numbers[0])
        updateCircleView(circleText2, numbers[1])
        updateCircleView(circleText3, numbers[2])
        updateCircleView(circleText4, numbers[3])
        updateCircleView(circleText5, numbers[4])
        updateCircleView(circleText6, numbers[5])
        updateCircleView(circleTextBonus, selectedResult.bonusNumber)

        // Round가 262보다 작은 경우 예외 처리
        if (selectedResult.round.toIntOrNull() ?: 0 < 262) {
            detailInfo_Mode.text = "미제공"
            detailInfo_Region.text = " "
            return
        }

        // Update detailInfo_Mode
        val modeText = listOfNotNull(
            if (selectedResult.autoCount > 0) "자동: ${selectedResult.autoCount}개" else null,
            if (selectedResult.manualCount > 0) "수동: ${selectedResult.manualCount}개" else null,
            if (selectedResult.semiAutoCount > 0) "반자동: ${selectedResult.semiAutoCount}개" else null
        ).joinToString(", ")

        detailInfo_Mode.text = if (modeText != "") modeText else "미당첨"

        // Update detailInfo_Region (내림차순 정렬)
        val sortedRegions = selectedResult.regionStats.entries
            .sortedByDescending { it.value.total }
            .map { (region, detail) ->
                val types = listOfNotNull(
                    if (detail.auto > 0) "자동 ${detail.auto}" else null,
                    if (detail.manual > 0) "수동 ${detail.manual}" else null,
                    if (detail.semiAuto > 0) "반자동 ${detail.semiAuto}" else null
                ).joinToString(", ")
                "$region ${detail.total}"
            }
            .joinToString(", ")

        detailInfo_Region.text = if (sortedRegions != "") sortedRegions.replace("동행", "온라인") else "(당첨자가 없습니다)"
    }

    private fun updateCircleView(circleText: TextView, number: Int) {
        circleText.text = number.toString()

        // 배경 설정
        val backgroundResource = when (number) {
            in 1..10 -> R.drawable.circle_background_1_10
            in 11..20 -> R.drawable.circle_background_11_20
            in 21..30 -> R.drawable.circle_background_21_30
            in 31..40 -> R.drawable.circle_background_31_40
            in 41..45 -> R.drawable.circle_background_41_45
            else -> 0 // 기본값
        }
        circleText.setBackgroundResource(backgroundResource)
    }

    private fun formatPrizeAmount(amount: Long?): String{
        if (amount == null) return "-"
        if (amount == 0L) return "0원"

        val eok = amount / 100_000_000L // 억단위
        val man = (amount % 100_000_000L) / 10_000L //만단위

        return when {
            eok > 0 && man > 0 -> "$eok 억 ${man} 만"
            eok > 0 -> "$eok 억"
            else -> "${man} 만"
        }
    }

    private fun parseDateData(): List<LottoResult> {
        val lottoResults = mutableListOf<LottoResult>()
        val inputStream = requireContext().assets.open("win_data_all.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val lines = reader.readLines()

        // 지역 이름을 순서대로 매핑
        val regionNames = listOf(
            "서울", "경기", "부산", "대구", "인천", "울산", "강원", "충북",
            "충남", "광주", "전북", "전남", "경북", "경남", "제주", "세종",
            "대전", "동행", "기타"
        )

        for (i in 1 until lines.size) {
            val tokens = lines[i].split(",")
            val round = tokens[0]
            val winningNumbers = tokens.subList(1, 7).map { it.toIntOrNull() ?: 0 }
            val bonusNumber = tokens[7].toIntOrNull() ?: 0
            val winnerCount = tokens[8].toIntOrNull() ?: 0
            val prizeAmount = tokens[9].toLongOrNull() ?: 0
            val rounddate = tokens[10]
            val autoCount = tokens[11].toIntOrNull() ?: 0
            val manualCount = tokens[12].toIntOrNull() ?: 0
            val semiAutoCount = tokens[13].toIntOrNull() ?: 0

            val regionStats = mutableMapOf<String, RegionDetail>()

            // 지역 데이터 시작 인덱스
            val regionDataStartIndex = 14

            // 각 지역별 자동/수동/반자동 데이터를 읽어서 RegionDetail 객체로 변환
            regionNames.forEachIndexed { index, regionName ->
                val baseIndex = regionDataStartIndex + index * 3
                if (baseIndex + 2 < tokens.size) {
                    val auto = tokens[baseIndex].toIntOrNull() ?: 0
                    val manual = tokens[baseIndex + 1].toIntOrNull() ?: 0
                    val semiAuto = tokens[baseIndex + 2].toIntOrNull() ?: 0
                    val total = auto + manual + semiAuto
                    if (total > 0) { // 총합이 0인 지역은 제외
                        regionStats[regionName] = RegionDetail(total, auto, manual, semiAuto)
                    }
                }
            }

            lottoResults.add(
                LottoResult(
                    round = round,
                    rounddate = rounddate,
                    winningNumbers = winningNumbers,
                    bonusNumber = bonusNumber,
                    winnerCount = winnerCount,
                    prizeAmount = prizeAmount,
                    autoCount = autoCount,
                    manualCount = manualCount,
                    semiAutoCount = semiAutoCount,
                    regionStats = regionStats
                )
            )
        }
        reader.close()
        return lottoResults
    }

}