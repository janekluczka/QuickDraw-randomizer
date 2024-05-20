package com.luczka.lotterymachine.fragments

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.luczka.lotterymachine.R
import com.luczka.lotterymachine.adapters.ItemListAdapter
import com.luczka.lotterymachine.viewmodels.LotteryViewModel

class LotteryFragment : Fragment() {

    companion object {
        private const val DEFAULT_FADE_DURATION = 500L
    }

    private val viewModel: LotteryViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private val itemListAdapter: ItemListAdapter = ItemListAdapter()

    private lateinit var toolbar: MaterialToolbar
    private lateinit var textInputText: TextInputEditText
    private lateinit var addButton: MaterialButton
    private lateinit var drawExtendedFloatingActionButton: ExtendedFloatingActionButton

    private val exitDialog: MaterialAlertDialogBuilder by lazy {
        MaterialAlertDialogBuilder(requireContext(), R.style.DialogWithTitleTheme)
            .setTitle(getString(R.string.dialog_exit_title))
            .setNegativeButton(getString(R.string.action_cancel)) { _, _ -> }
            .setPositiveButton(getString(R.string.action_exit)) { _, _ ->
                requireActivity().finish()
            }
    }

    private val itemTouchHelper = ItemTouchHelper(
        object : ItemTouchHelper.SimpleCallback(
            /* dragDirs = */ ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            /* swipeDirs = */ ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.absoluteAdapterPosition
                val toPosition = target.absoluteAdapterPosition

                viewModel.moveItem(fromPosition, toPosition)
                itemListAdapter.notifyItemMoved(fromPosition, toPosition)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                viewModel.removeItemAt(position)
                itemListAdapter.notifyItemRemoved(position)
            }
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lottery, container, false)

        toolbar = view.findViewById(R.id.fragment_lottery_toolbar)
        recyclerView = view.findViewById(R.id.fragment_lottery_rv_items)
        textInputText = view.findViewById(R.id.fragment_lottery_et_new_item)
        addButton = view.findViewById(R.id.fragment_lottery_btn_add)
        drawExtendedFloatingActionButton = view.findViewById(R.id.fragment_lottery_fab_draw)

        return view
    }

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

        toolbar.isTitleCentered = true
        toolbar.title = getString(R.string.app_name)

        toolbar.inflateMenu(R.menu.lottery_fragment_menu)
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_select_theme -> {
                    openThemeSelectorBottomSheet()
                    true
                }

                else -> false
            }
        }

        itemTouchHelper.attachToRecyclerView(recyclerView)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemListAdapter
        }

        viewModel.itemsLiveData.observe(viewLifecycleOwner) { itemsList ->
            itemListAdapter.submitList(itemsList)
            recyclerView.scrollToPosition(viewModel.getItemCount())

            if (itemsList.isNotEmpty()) {
                drawExtendedFloatingActionButton.fadeVisibility(View.VISIBLE)
            } else {
                drawExtendedFloatingActionButton.fadeVisibility(View.GONE)
            }
        }

        viewModel.removedItem.observe(viewLifecycleOwner) { removedItem ->
            if (removedItem != null) {
                Snackbar
                    .make(
                        view,
                        getString(R.string.snackbar_removed_title),
                        Snackbar.LENGTH_LONG
                    )
                    .setAction(getString(R.string.action_undo)) {
                        viewModel.undoRemoveItem(removedItem)
                    }
                    .setAnchorView(R.id.fragment_lottery_divider_bottom)
                    .show()
            }
        }

        addButton.setOnClickListener {
            onAddNewItem()
        }

        drawExtendedFloatingActionButton.setOnClickListener {
            onDrawItem()
        }
    }

    private fun openThemeSelectorBottomSheet() {
        activity?.supportFragmentManager?.let { supportFragmentManager ->
            val themeModalBottomSheet = ThemeModalBottomSheet()
            themeModalBottomSheet.show(supportFragmentManager, ThemeModalBottomSheet.TAG)
        }
    }

    private fun View.fadeVisibility(
        visibility: Int,
        duration: Long = DEFAULT_FADE_DURATION
    ) {
        val transition: Transition = Fade()
        transition.duration = duration
        transition.addTarget(this)
        TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
        this.visibility = visibility
    }

    private fun onAddNewItem() {
        textInputText.text?.let { input ->
            if (input.isNotBlank()) {
                viewModel.addItem(input.toString())
                input.clear()
            }
        }
    }

    private fun onDrawItem() {
        viewModel.drawItem(
            onNoItems = {

            },
            onOneItem = {
                navigateToResult(drawnValue = getString(R.string.item_seriously))
            },
            onMultipleItems = { drawnValue ->
                navigateToResult(drawnValue = drawnValue)
            }
        )
    }

    private fun navigateToResult(drawnValue: String) {
        val action = LotteryFragmentDirections.actionLotteryFragmentToResultFragment(drawnValue)
        findNavController().navigate(action)
    }

}

class ThemeModalBottomSheet : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "ThemeModalBottomSheet"
    }

    private lateinit var radioGroup: RadioGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.modal_bottom_sheet_content, container, false)

        radioGroup = view.findViewById(R.id.modal_bottom_sheet_content_radio_light)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uiModeManager =
            requireContext().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

        val currentMode = resources.configuration.uiMode

        when (currentMode) {
            UiModeManager.MODE_NIGHT_NO -> radioGroup.check(R.id.modal_bottom_sheet_content_radio_light)
            UiModeManager.MODE_NIGHT_YES -> radioGroup.check(R.id.modal_bottom_sheet_content_radio_dark)
            else -> radioGroup.check(R.id.modal_bottom_sheet_content_radio_system)
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.modal_bottom_sheet_content_radio_light -> UiModeManager.MODE_NIGHT_NO
                R.id.modal_bottom_sheet_content_radio_dark -> UiModeManager.MODE_NIGHT_YES
                R.id.modal_bottom_sheet_content_radio_system -> UiModeManager.MODE_NIGHT_AUTO

                else -> {
                    return@setOnCheckedChangeListener
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                uiModeManager.setApplicationNightMode(mode)
            } else {
                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }
    }

}