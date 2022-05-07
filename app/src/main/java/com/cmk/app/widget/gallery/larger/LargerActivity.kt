package com.cmk.app.widget.gallery.larger

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.cmk.app.databinding.ActivityLargerBinding

class LargerActivity<T> : AppCompatActivity() {

    private lateinit var binding: ActivityLargerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLargerBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
    }
}