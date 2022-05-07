package com.cmk.app.ui.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.cmk.app.R
import com.cmk.app.base.BaseFragment
import com.cmk.app.base.dataBinding
import com.cmk.app.databinding.FragmentLoginInputBinding
import com.cmk.app.listener.textWatcher
import com.cmk.app.viewmodel.LoginViewModel

/**
 * @Author: romens
 * @Date: 2019-12-4 9:08
 * @Desc:
 */
class LoginInputFragment : BaseFragment() {

    private var _binding: FragmentLoginInputBinding? = null
    private val binding get() = _binding!!

    //    private val viewModel by navGraphViewModels<LoginViewModel>(R.id.navigation_login)
    private val viewModel by activityViewModels<LoginViewModel>()
    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvRegedit.setOnClickListener {
            findNavController().navigate(R.id.action_loginInputFragment_to_regeditFragment)
        }

        binding.etUsername.textWatcher {
            afterTextChanged {
                viewModel.loginDataChange(
                    binding.etUsername.text.toString(),
                    binding.etPassword.text.toString()
                )
            }
        }

        binding.etPassword.textWatcher {
            afterTextChanged {
                viewModel.loginDataChange(
                    binding.etUsername.text.toString(),
                    binding.etPassword.text.toString()
                )
            }
        }

        binding.tvSubmit.setOnClickListener {
            login()
        }
    }

    private fun login() {
        viewModel.login(binding.etUsername.text.toString(), binding.etPassword.text.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}