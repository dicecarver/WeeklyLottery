package com.example.myapplication

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class LottoResultAdapter(
    private val results: List<LottoResult>,
    private val onItemClicked: (LottoResult) -> Unit
) : RecyclerView.Adapter<LottoResultAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION // 선택된 항목 위치

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roundTextView: TextView = view.findViewById(R.id.roundText)
        val numbersTextView: TextView = view.findViewById(R.id.numbersText)
        val bonusTextView: TextView = view.findViewById(R.id.bonusText)
        val winnerCountTextView: TextView = view.findViewById(R.id.winnerCountText)
        val prizeAmountTextView: TextView = view.findViewById(R.id.prizeAmountText)

        fun bind(lottoResult: LottoResult, isSelected: Boolean) {
            roundTextView.text = lottoResult.round
            numbersTextView.text = lottoResult.winningNumbers.joinToString(", ")
            bonusTextView.text = lottoResult.bonusNumber.toString()
            winnerCountTextView.text = lottoResult.winnerCount.toString()
            prizeAmountTextView.text = lottoResult.prizeAmount.toString()

            // 선택된 상태에 따라 배경색 변경
            if (isSelected) {
                // 선택된 항목 스타일
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.gold))
                setTextColorAndStyle(Color.BLACK, true) // 흰색 글자와 Bold 스타일
            } else {
                // 기본 스타일
                itemView.setBackgroundColor(Color.TRANSPARENT)
                setTextColorAndStyle(Color.WHITE, false) // 검정 글자와 기본 스타일
            }
        }
        private fun setTextColorAndStyle(color: Int, isBold: Boolean) {
            val textStyle = if (isBold) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL

            roundTextView.setTextColor(color)
            roundTextView.setTypeface(null, textStyle)

            numbersTextView.setTextColor(color)
            numbersTextView.setTypeface(null, textStyle)

            bonusTextView.setTextColor(color)
            bonusTextView.setTypeface(null, textStyle)

            winnerCountTextView.setTextColor(color)
            winnerCountTextView.setTypeface(null, textStyle)

            prizeAmountTextView.setTextColor(color)
            prizeAmountTextView.setTypeface(null, textStyle)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lotto_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val result = results[position]
        holder.bind(result, position == selectedPosition) // 선택 여부 전달
        holder.roundTextView.text = result.round
        holder.numbersTextView.text = result.winningNumbers.joinToString(", ")
        holder.bonusTextView.text = result.bonusNumber.toString()
        holder.winnerCountTextView.text = result.winnerCount.toString()
        //holder.prizeAmountTextView.text = result.prizeAmount.toString()
        holder.prizeAmountTextView.text = formatPrizeAmount(result.prizeAmount)

        // 항목 클릭 시 선택된 항목 업데이트
        holder.itemView.setOnClickListener {
            val adapterPosition = holder.bindingAdapterPosition // 동적 위치 조회

            if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener

            val previousPosition = selectedPosition
            selectedPosition = adapterPosition

            // 이전 선택된 항목과 현재 선택된 항목을 업데이트
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            // 선택된 항목의 데이터 전달
            onItemClicked(result)
        }
    }

    fun formatPrizeAmount(amount: Long?): String{
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


    override fun getItemCount(): Int = results.size
}
