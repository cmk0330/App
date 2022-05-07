package com.cmk.app.ui.activity.test

import android.os.Bundle
import android.view.LayoutInflater
import com.cmk.app.base.BaseActivity
import com.cmk.app.databinding.ActivityMotionLayoutBinding

class MotionLayoutActivity :BaseActivity() {
    private lateinit var binding:ActivityMotionLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMotionLayoutBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
    }
}