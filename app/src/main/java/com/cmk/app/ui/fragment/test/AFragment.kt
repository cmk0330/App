package com.cmk.app.ui.fragment.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.cmk.app.base.BaseFragment
import com.cmk.app.databinding.FragmentABinding
import com.cmk.app.viewmodel.CommunicationViewModel

class AFragment :BaseFragment() {

    private lateinit var binding:FragmentABinding
    private val viewModel by activityViewModels<CommunicationViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      binding = FragmentABinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.progressLiveData.value = "来自A-->$progress"
                setFragmentResult("REQUEST_KEY_A", bundleOf("BUNDLE_KEY_1" to "$progress",
                    "BUNDLE_KEY_2" to "来自fragmentA"))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.textView.setOnClickListener {
            setFragmentResult("REQUEST_KEY_A", bundleOf("BUNDLE_KEY_1" to "result",
            "BUNDLE_KEY_2" to "来自fragmentA"))
        }

        setFragmentResultListener("REQUEST_KEY_B") {resultKey, bundle ->
            binding.textView.text = "$resultKey-->${bundle.getString("BUNDLE_KEY_1")},${bundle.getString("BUNDLE_KEY_2")}"
        }

//        viewModel.progressLiveData.observe(viewLifecycleOwner, Observer {
//            binding.textView.text = it
//        })
    }

}