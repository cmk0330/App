package com.cmk.app.ui.activity.test

import android.os.Bundle
import android.view.LayoutInflater
import com.cmk.app.base.BaseActivity
import com.cmk.app.databinding.ActivityCommunicationBinding

class CommunicationActivity:BaseActivity() {
    private lateinit var binding: ActivityCommunicationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunicationBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
    }
}