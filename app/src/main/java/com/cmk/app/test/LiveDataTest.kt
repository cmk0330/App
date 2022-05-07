package com.cmk.app.test

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.cmk.app.R

/**
 * @Author: romens
 * @Date: 2020-1-13 14:55
 * @Desc:
 */
class LiveDataTest :AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_livedata_test)
        supportFragmentManager.beginTransaction().replace(R.id.fl_1, LiveDataTestFragment()).commit()
        supportFragmentManager.beginTransaction().replace(R.id.fl_2, LiveDataTestFragment1()).commit()

        findViewById<SeekBar>(R.id.seekBar).setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                LiveDataTestVM.instance.seekLiveData.value = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }
}