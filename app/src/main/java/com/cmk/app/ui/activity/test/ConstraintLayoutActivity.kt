package com.cmk.app.ui.activity.test

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.LinearInterpolator
import com.cmk.app.base.BaseActivity
import com.cmk.app.databinding.ActivityConstraintlayoutBinding

class ConstraintLayoutActivity : BaseActivity() {
    private lateinit var binding: ActivityConstraintlayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConstraintlayoutBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val animator = ValueAnimator.ofFloat(0f, 360f)
        animator.repeatMode = ValueAnimator.RESTART
        animator.duration  = 2000
        animator.interpolator = LinearInterpolator()
        animator.repeatCount = ValueAnimator.INFINITE
        animator.addUpdateListener {
            binding.layer.rotation = it.animatedValue as Float
        }
        binding.layer.setOnClickListener {
            animator.start()
        }

        binding.flow
    }
}