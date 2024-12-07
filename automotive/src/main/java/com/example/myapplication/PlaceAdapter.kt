package com.example.myapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class PlaceAdapter(
    private var places: List<Place>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(place: Place)
    }

    private var selectedPosition: Int = RecyclerView.NO_POSITION
    private val TAG = "PlaceAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        Log.d(TAG, "onCreateViewHolder called")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder called for position $position")
        val place = places[position]
        holder.bind(place, position == selectedPosition)
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount: ${places.size}")
        return places.size
    }

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.placeNameTextView)
        private val addressTextView: TextView = itemView.findViewById(R.id.placeAddressTextView)
        private val firstPrizeCountTextView: TextView = itemView.findViewById(R.id.firstPrizeCountTextView)
        private val secondPrizeCountTextView: TextView = itemView.findViewById(R.id.secondPrizeCountTextView)
        private val firstRecentPrizeTextView: TextView = itemView.findViewById(R.id.firstRecentPrizeText)
        private val secondRecentPrizeTextView: TextView = itemView.findViewById(R.id.secondRecentPrizeText)

        private val distanceTextView: TextView = itemView.findViewById(R.id.distanceTextView)

        private val selectedCardViewColor = ContextCompat.getColor(itemView.context, R.color.selected_item)
        private val selectedCardViewTextColor = ContextCompat.getColor(itemView.context, R.color.selected_textitem)

        private val defaultColor = ContextCompat.getColor(itemView.context, R.color.background_black)
        //private val defaultColor = ContextCompat.bac(itemView.context, R.color.background_black)

        private val unselectedTextColor = ContextCompat.getColor(itemView.context, android.R.color.white)
        private val cardView: CardView = itemView.findViewById(R.id.CardView)
        private val placenametextView: TextView = itemView.findViewById(R.id.placeNameTextView)
        private val placeaddresstextview: TextView = itemView.findViewById(R.id.placeAddressTextView)

        private val selectedFirstRecentTextColor = ContextCompat.getColor(itemView.context, R.color.omrPen_second_red)
        private val selectedSecondRecentTextColor = ContextCompat.getColor(itemView.context, R.color.ball_stroke_31_40)
        private val unselectedFirstRecentTextColor = ContextCompat.getColor(itemView.context, R.color.gold)
        private val unselectedSecondRecentTextColor = ContextCompat.getColor(itemView.context, R.color.silver)
//

        fun bind(place: Place, isSelected: Boolean) {
            Log.d(TAG, "bind: ${place.name}, isSelected: $isSelected")
            nameTextView.text = place.name
            addressTextView.text = place.address
            firstPrizeCountTextView.text = place.firstPrizeCount.toString()
            secondPrizeCountTextView.text = place.secondPrizeCount.toString()
//            distanceTextView.text = place.additionalInfo

//            distanceTextView.text = place.distance.toString()
            //distanceTextView.text = String.format("%.1f km", place.distance!! / 1000)
            distanceTextView.text = if (place.distance != null) {
                String.format("%.1f km", place.distance!! / 1000)
            } else {
                "계산 중..."
            }
            firstRecentPrizeTextView.text = "최근 1등: ${place.firstPrizeRecentText.toString()}"
            secondRecentPrizeTextView.text = "최근 2등: ${place.secondPrizeRecentText.toString()}"

//            itemView.setBackgroundColor(if (isSelected) selectedColor else defaultColor)
            // 선택 여부에 따라 색상 변경
            if (isSelected) {
                cardView.setBackgroundResource(R.drawable.card_selectecd_background)
                placenametextView.setTextColor(selectedCardViewTextColor)
                placeaddresstextview.setTextColor(selectedCardViewTextColor)
                distanceTextView.setTextColor(selectedCardViewTextColor)
                firstPrizeCountTextView.setTextColor(selectedCardViewTextColor)
                secondPrizeCountTextView.setTextColor(selectedCardViewTextColor)
                firstRecentPrizeTextView.setTextColor(selectedFirstRecentTextColor)
                secondRecentPrizeTextView.setTextColor(selectedSecondRecentTextColor)
            } else {
                cardView.setBackgroundResource(R.drawable.card_default_background)
                placenametextView.setTextColor(unselectedTextColor)
                placeaddresstextview.setTextColor(unselectedTextColor)
                distanceTextView.setTextColor(unselectedTextColor)
                firstPrizeCountTextView.setTextColor(unselectedTextColor)
                secondPrizeCountTextView.setTextColor(unselectedTextColor)
                firstRecentPrizeTextView.setTextColor(unselectedFirstRecentTextColor)
                secondRecentPrizeTextView.setTextColor(unselectedSecondRecentTextColor)
            }

            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition

                Log.d(TAG, "Item clicked: ${place.name}, adapterPosition: $adapterPosition")

                if (previousPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(previousPosition)
                }
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(adapterPosition)
                    itemClickListener.onItemClick(place)
                }
            }
        }
    }

    fun updatePlaces(filteredPlaces: List<Place>) {
        this.places = filteredPlaces
        notifyDataSetChanged()
    }
}
