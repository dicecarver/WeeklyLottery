package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LottoResultAdapter(private val lottoResults: List<LottoResult>) :
    RecyclerView.Adapter<LottoResultAdapter.LottoResultViewHolder>() {

    // ViewHolder 클래스
    inner class LottoResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val roundText: TextView = itemView.findViewById(R.id.roundText)
        private val numbersText: TextView = itemView.findViewById(R.id.numbersText)
        private val bonusText: TextView = itemView.findViewById(R.id.bonusText)
        private val winnerCountText: TextView = itemView.findViewById(R.id.winnerCountText)
        private val prizeAmountText: TextView = itemView.findViewById(R.id.prizeAmountText)

        // 로또 결과 데이터를 바인딩
        fun bind(result: LottoResult) {
            roundText.text = "${result.round}회"
            numbersText.text = result.winningNumbers.joinToString(" ") { it.toString() }
            bonusText.text = "${result.bonusNumber}"
            winnerCountText.text = "${result.winnerCount}명"

            // 당첨금액에 콤마를 추가하여 표시
            //val formattedPrizeAmount = String.format("%,d", result.prizeAmount.toInt())
            val formattedPrizeAmount = String.format("%,d", result.prizeAmount)
            prizeAmountText.text = formattedPrizeAmount
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LottoResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lotto_result, parent, false)
        return LottoResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: LottoResultViewHolder, position: Int) {
        val result = lottoResults[position]
        holder.bind(result)
    }

    override fun getItemCount(): Int = lottoResults.size
}
