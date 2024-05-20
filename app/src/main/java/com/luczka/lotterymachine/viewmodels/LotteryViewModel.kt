package com.luczka.lotterymachine.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.luczka.lotterymachine.models.RemovedItem

class LotteryViewModel : ViewModel() {

    private val _itemsLiveData = MutableLiveData<MutableList<String>>(mutableListOf())
    val itemsLiveData: LiveData<MutableList<String>> = _itemsLiveData

    private val _removedItem = MutableLiveData<RemovedItem?>()
    val removedItem: LiveData<RemovedItem?> = _removedItem

    fun addItem(item: String) {
        val updatedList = _itemsLiveData.value ?: mutableListOf()
        updatedList.add(item)
        _itemsLiveData.value = updatedList
    }

    fun removeItemAt(position: Int) {
        val updatedList = _itemsLiveData.value ?: mutableListOf()
        if (position in updatedList.indices) {
            val item = updatedList.removeAt(position)
            _itemsLiveData.value = updatedList
            _removedItem.value = RemovedItem(position, item)
        }
    }

    fun undoRemoveItem(removedItem: RemovedItem) {
        val currentList = _itemsLiveData.value ?: mutableListOf()
        currentList.add(removedItem.position, removedItem.item)
        _itemsLiveData.value = currentList
        _removedItem.value = null
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val updatedList = _itemsLiveData.value ?: mutableListOf()
        if (fromPosition in updatedList.indices && toPosition in updatedList.indices) {
            val item = updatedList.removeAt(fromPosition)
            updatedList.add(toPosition, item)
            _itemsLiveData.value = updatedList
        }
    }

    fun itemListIsEmpty(): Boolean = _itemsLiveData.value?.isEmpty() ?: true

    fun getItemCount(): Int = _itemsLiveData.value?.size ?: 0

    fun drawItem(
        onNoItems: () -> Unit,
        onOneItem: () -> Unit,
        onMultipleItems: (String) -> Unit
    ) {
        val items = _itemsLiveData.value ?: return
        when (items.size) {
            0 -> onNoItems()
            1 -> onOneItem()
            else -> {
                val randomItem = items.random()
                onMultipleItems(randomItem)
            }
        }
    }
}