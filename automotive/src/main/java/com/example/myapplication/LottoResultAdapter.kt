package com.example.myapplication

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
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
        val numbersTextView_1: TextView = view.findViewById(R.id.numbersText_1)
        val numbersTextView_2: TextView = view.findViewById(R.id.numbersText_2)
        val numbersTextView_3: TextView = view.findViewById(R.id.numbersText_3)
        val numbersTextView_4: TextView = view.findViewById(R.id.numbersText_4)
        val numbersTextView_5: TextView = view.findViewById(R.id.numbersText_5)
        val numbersTextView_6: TextView = view.findViewById(R.id.numbersText_6)
        val bonusTextView: TextView = view.findViewById(R.id.bonusText)
        val winnerCountTextView: TextView = view.findViewById(R.id.winnerCountText)
        val prizeAmountTextView: TextView = view.findViewById(R.id.prizeAmountText)

        fun bind(lottoResult: LottoResult, isSelected: Boolean) {
            val roundText = lottoResult.round
            val roundDateText = "(${lottoResult.rounddate})"
            val combinedText = "$roundText\n$roundDateText"

            // SpannableString으로 글자 크기와 스타일 설정
            val spannable = SpannableString(combinedText)

            // round의 스타일 설정
            spannable.setSpan(
                RelativeSizeSpan(1.5f), // 1.2배 크기
                0,
                roundText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable.setSpan(
                android.text.style.StyleSpan(android.graphics.Typeface.BOLD), // 굵게
                0,
                roundText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // rounddate의 스타일 설정
            spannable.setSpan(
                RelativeSizeSpan(0.7f), // 0.8배 크기
                combinedText.indexOf(roundDateText),
                combinedText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable.setSpan(
                android.text.style.StyleSpan(android.graphics.Typeface.ITALIC), // 이탤릭체
                combinedText.indexOf(roundDateText),
                combinedText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // roundTextView에 SpannableString 설정
            roundTextView.text = spannable
            numbersTextView_1.text = lottoResult.winningNumbers[0].toString()
            numbersTextView_2.text = lottoResult.winningNumbers[1].toString()
            numbersTextView_3.text = lottoResult.winningNumbers[2].toString()
            numbersTextView_4.text = lottoResult.winningNumbers[3].toString()
            numbersTextView_5.text = lottoResult.winningNumbers[4].toString()
            numbersTextView_6.text = lottoResult.winningNumbers[5].toString()
            bonusTextView.text = lottoResult.bonusNumber.toString()
            winnerCountTextView.text = lottoResult.winnerCount.toString()
            prizeAmountTextView.text = formatPrizeAmount(lottoResult.prizeAmount)

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
        private fun setTextColorAndStyle(color: Int, isBold: Boolean) {
            val textStyle = if (isBold) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL

            val this_textSize = 26f
            // TextView 스타일 설정
            roundTextView.setTextColor(color)
            roundTextView.setTypeface(null, textStyle)
            roundTextView.setTextSize(this_textSize)

            numbersTextView_1.setTextColor(color)
            numbersTextView_1.setTypeface(null, textStyle)
            numbersTextView_1.setTextSize(this_textSize)
            numbersTextView_2.setTextColor(color)
            numbersTextView_2.setTypeface(null, textStyle)
            numbersTextView_2.setTextSize(this_textSize)
            numbersTextView_3.setTextColor(color)
            numbersTextView_3.setTypeface(null, textStyle)
            numbersTextView_3.setTextSize(this_textSize)
            numbersTextView_4.setTextColor(color)
            numbersTextView_4.setTypeface(null, textStyle)
            numbersTextView_4.setTextSize(this_textSize)
            numbersTextView_5.setTextColor(color)
            numbersTextView_5.setTypeface(null, textStyle)
            numbersTextView_5.setTextSize(this_textSize)
            numbersTextView_6.setTextColor(color)
            numbersTextView_6.setTypeface(null, textStyle)
            numbersTextView_6.setTextSize(this_textSize)

            bonusTextView.setTextColor(color)
            bonusTextView.setTypeface(null, textStyle)
            bonusTextView.setTextSize(this_textSize)

            winnerCountTextView.setTextColor(color)
            winnerCountTextView.setTypeface(null, textStyle)
            winnerCountTextView.setTextSize(this_textSize)

            prizeAmountTextView.setTextColor(color)
            prizeAmountTextView.setTypeface(null, textStyle)
            prizeAmountTextView.setTextSize(this_textSize)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lotto_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]
        holder.bind(result, position == selectedPosition) // 선택 여부와 함께 바인딩

        // 항목 클릭 시 선택된 항목 업데이트
        holder.itemView.setOnClickListener {
            val adapterPosition = holder.bindingAdapterPosition
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


    override fun getItemCount(): Int = results.size
}
