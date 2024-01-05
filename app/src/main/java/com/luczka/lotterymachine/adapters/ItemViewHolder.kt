package com.luczka.lotterymachine.adapters

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.luczka.lotterymachine.R
import com.luczka.lotterymachine.models.Item

class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val itemTextView: TextView

    private lateinit var item: Item

    init {
        itemTextView = view.findViewById(R.id.item_text)
    }

    fun bind(item: Item) {
        this.item = item
        itemTextView.text = item.text
    }
}