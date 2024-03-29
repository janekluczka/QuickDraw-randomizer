package com.luczka.lotterymachine.fragments

import android.os.Bundle
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.luczka.lotterymachine.R
import com.luczka.lotterymachine.adapters.ItemListAdapter
import com.luczka.lotterymachine.viewmodels.LotteryViewModel

class LotteryFragment : Fragment() {

    private val viewModel: LotteryViewModel by viewModels()

    private lateinit var itemRecyclerView: RecyclerView
    private lateinit var textInputText: TextInputEditText
    private lateinit var addButton: MaterialButton
    private lateinit var drawExtendedFloatingActionButton: ExtendedFloatingActionButton

    private val exitDialog: MaterialAlertDialogBuilder by lazy {
        MaterialAlertDialogBuilder(requireContext(), R.style.DialogWithTitleTheme)
            .setTitle(resources.getString(R.string.exit_dialog_title))
            .setNegativeButton(resources.getString(R.string.exit_dialog_stay)) { _, _ -> }
            .setPositiveButton(resources.getString(R.string.exit_dialog_exit)) { _, _ -> requireActivity().finish() }
    }

    private val itemTouchHelper = ItemTouchHelper(
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.removeItemAt(viewHolder.absoluteAdapterPosition)
            }
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_lottery, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (viewModel.itemListIsEmpty()) {
                requireActivity().finish()
            } else {
                exitDialog.show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemRecyclerView = view.findViewById(R.id.fragment_lottery_rv_items)
        textInputText = view.findViewById(R.id.fragment_lottery_et_new_item)
        addButton = view.findViewById(R.id.fragment_lottery_btn_add)
        drawExtendedFloatingActionButton = view.findViewById(R.id.fragment_lottery_fab_draw)

        itemTouchHelper.attachToRecyclerView(itemRecyclerView)

        itemRecyclerView.layoutManager = LinearLayoutManager(context)
        itemRecyclerView.adapter = ItemListAdapter()

        viewModel.itemsLiveData.observe(viewLifecycleOwner) { itemsList ->
            (itemRecyclerView.adapter as ItemListAdapter).submitList(itemsList.toList())

            if (itemsList.isNotEmpty()) {
                drawExtendedFloatingActionButton.fadeVisibility(View.VISIBLE)
            } else {
                drawExtendedFloatingActionButton.fadeVisibility(View.GONE)
            }
        }

        addButton.setOnClickListener {
            addNewItem()
        }

        drawExtendedFloatingActionButton.setOnClickListener {
            drawItemAndNavigate()
        }
    }

    private fun View.fadeVisibility(visibility: Int, duration: Long = 500) {
        val transition: Transition = Fade()
        transition.duration = duration
        transition.addTarget(this)
        TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
        this.visibility = visibility
    }

    private fun addNewItem() {
        textInputText.text?.let { text ->
            if (text.isNotBlank()) {
                viewModel.addItem(text.toString())
                text.clear()
            }
        }
    }

    private fun drawItemAndNavigate() {
        viewModel.drawItem(
            onNoItems = {

            },
            onOneItem = {
                navigateToResult(item = requireContext().getString(R.string.seriously))
            },
            onMultipleItems = { item ->
                navigateToResult(item = item)
            }
        )
    }

    private fun navigateToResult(item: String) {
        val action = LotteryFragmentDirections.actionLotteryFragmentToResultFragment(item)
        findNavController().navigate(action)
    }

}