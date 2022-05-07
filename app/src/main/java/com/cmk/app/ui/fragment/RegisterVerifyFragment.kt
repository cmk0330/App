package com.cmk.app.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.cmk.app.base.BaseFragment
import com.cmk.app.databinding.FragmentRegisterVerifyBinding
import com.cmk.app.viewmodel.LoginViewModel

class RegisterVerifyFragment : BaseFragment() {

    private  var _binding: FragmentRegisterVerifyBinding ?=null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterVerifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSubmit.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvCount.setOnClickListener {
            viewModel.sendCode()
        }
    }

    @SuppressLint("SetTextI18n")
    override suspend fun subscribe() {
        super.subscribe()
        viewModel.countDownLiveData.observe(this, Observer {
            binding.tvCount.text = "$it s"
            if (it == -1) {
                binding.tvCount.text = "重新发送"
                binding.tvCount.isEnabled = true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}