package com.luczka.lotterymachine.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.MaterialToolbar
import com.luczka.lotterymachine.R

class ResultFragment : Fragment() {

    private val args: ResultFragmentArgs by navArgs()

    private lateinit var toolbar: MaterialToolbar
    private lateinit var resultText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_result, container, false)

        toolbar = view.findViewById(R.id.fragment_result_toolbar)
        resultText = view.findViewById(R.id.fragment_result_txt_result)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.navigationIcon = ResourcesCompat.getDrawable(
            resources,
            R.drawable.ic_baseline_arrow_back,
            null
        )
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        resultText.text = args.result
    }

}