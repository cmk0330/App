package com.cmk.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.cmk.app.R
import com.cmk.app.base.BaseFragment
import com.cmk.app.databinding.FragmentRegisterBinding
import com.cmk.app.viewmodel.LoginViewModel

/**
 * @Author: romens
 * @Date: 2019-12-4 9:08
 * @Desc:
 */
class RegisterFragment : BaseFragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    //    private val viewModel by navGraphViewModels<LoginViewModel>(R.id.navigation_login)
    private val viewModel by activityViewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvGoLogin.setOnClickListener {
            findNavController().navigateUp()
        }


        binding.tvNext.setOnClickListener {
//            findNavController().navigate(R.id.action_registerFragment_to_registerVerifyFragment)
//            viewModel.sendCode()
            viewModel.regedit(
                binding.etPhone.text.toString(),
                binding.etPassword.text.toString(),
                binding.etPassword.text.toString()
            );
        }

//        binding.etUsername.textWatcher {
//            afterTextChanged {
//            }
//        }
//
//        binding.tvSubmit.setOnClickListener {
//            Toast.makeText(context, "ddd", Toast.LENGTH_SHORT).show()
//            viewModel.regedit(
//                binding.etUsername.text.toString(),
//                binding.etPassword.text.toString(),
//                binding.etRePassword.text.toString()
//            )
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}