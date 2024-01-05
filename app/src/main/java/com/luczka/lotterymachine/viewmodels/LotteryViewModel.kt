package com.luczka.lotterymachine.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.luczka.lotterymachine.R
import com.luczka.lotterymachine.models.Item
import kotlin.random.Random

class LotteryViewModel : ViewModel() {

    private var counter = 0

    private val items = ArrayList<Item>()

    var isShowingExitDialog = false

    private val _itemsLiveData = MutableLiveData<ArrayList<Item>>()
    val itemsLiveData: LiveData<ArrayList<Item>> = _itemsLiveData

    fun addItem(text: String) {
        items.add(Item(id = counter, text = text))
        counter += 1
        _itemsLiveData.postValue(items)
    }

    fun removeItemAt(position: Int) {
        items.removeAt(position)
        _itemsLiveData.postValue(items)
    }

    fun drawItem(context: Context): String? {
        return when (items.size) {
            0 -> null
            1 -> context.getString(R.string.seriously)
            else -> {
                val randomItemIndex = Random.nextInt(items.size)
                items[randomItemIndex].text
            }
        }
    }

    fun itemListIsNotEmpty(): Boolean = _itemsLiveData.value?.isNotEmpty() ?: false

}