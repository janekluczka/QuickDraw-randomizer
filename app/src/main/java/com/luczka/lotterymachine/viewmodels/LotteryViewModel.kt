package com.luczka.lotterymachine.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.luczka.lotterymachine.models.Item

class LotteryViewModel : ViewModel() {

    private var counter = 0

    private val items = ArrayList<Item>()

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

    fun drawItem(
        onNoItems: () -> Unit,
        onOneItem: () -> Unit,
        onMultipleItems: (String) -> Unit
    ) {
        when (items.size) {
            0 -> onNoItems()
            1 -> onOneItem()
            else -> {
                val randomItem = items.random().text
                onMultipleItems(randomItem)
            }
        }
    }

    fun itemListIsEmpty(): Boolean = _itemsLiveData.value?.isEmpty() ?: true

}