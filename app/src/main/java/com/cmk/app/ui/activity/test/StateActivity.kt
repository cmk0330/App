package com.cmk.app.ui.activity.test

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.cmk.app.base.BaseActivity
import com.cmk.app.databinding.ActivityStateBinding
import com.cmk.app.net.onCatch
import com.cmk.app.viewmodel.StateViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

class StateActivity : BaseActivity() {
    private lateinit var binding: ActivityStateBinding
    private val viewModel by viewModels<StateViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStateBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        var state = true
        binding.btnChange.setOnClickListener {
             viewModel.changeState(state)
            state = !state
        }

        binding.btnChange2.setOnClickListener {
            viewModel.changeState2()
        }

//        lifecycleScope.launchWhenCreated {
//            viewModel.loading.collect {
//                binding.tvState.text = if (it == true) "状态1" else "状态2 "
//            }
//        }
//
        lifecycleScope.launchWhenCreated {
            viewModel.loading2.collect {
                binding.tvState.text = if (it) "状态1" else "状态2 "
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.viewState
                .onStart {

                }.onCompletion {

                }.onCatch()
                .collect {
                    Log.e("State--->", it.toString())
                    binding.tvState.text = if (it.state) "状态1" else "状态2 "
                }
        }
    }
}