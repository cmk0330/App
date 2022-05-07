package com.cmk.app.test

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.cmk.app.R

/**
 * @Author: romens
 * @Date: 2020-1-13 15:01
 * @Desc:
 */
class LiveDataTestFragment1:Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_livedata_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val seekBar = view.findViewById<SeekBar>(R.id.seekBar)

        seekBar.setOnSeekBarChangeListener(object :OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        LiveDataTestVM.instance.seekLiveData.observe(viewLifecycleOwner, Observer {
            seekBar.progress = it
            Log.e("LiveDataTestFragment1", "$it")
        })

    }
}