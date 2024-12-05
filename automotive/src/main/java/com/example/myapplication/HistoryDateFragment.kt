package com.example.myapplication

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

        // 보너스 번호 업데이트
        updateCircleView(circleTextBonus, selectedResult.bonusNumber)
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
        val inputStream = requireContext().assets.open("win_data_all.csv") // 파일 이름을 win_data_all.csv로 설정
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
            val dateround = tokens[10]  // Date

            // LottoResult 객체 생성
            val result = LottoResult(round, dateround, winningNumbers, bonusNumber, winnerCount, prizeAmount)
            lottoResults.add(result)
        }
        reader.close()

        return lottoResults
    }

}