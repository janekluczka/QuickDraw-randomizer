package com.luczka.lotterymachine.adapters

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.luczka.lotterymachine.R

class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val valueTextView: TextView = view.findViewById(R.id.item_card_txt_value)

    fun bind(item: String) {
        valueTextView.text = item
    }
}