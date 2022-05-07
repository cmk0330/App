package com.cmk.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.cmk.app.base.BaseFragment
import com.cmk.app.databinding.FragmentForgetPassBinding
import com.cmk.app.viewmodel.LoginViewModel

/**
 * @Author: romens
 * @Date: 2019-12-4 10:42
 * @Desc:
 */
class ForgetPassFragment : BaseFragment() {

    private val binding get() = _binding!!
    private var _binding: FragmentForgetPassBinding? = null

    //    private val viewModel by navGraphViewModels<LoginViewModel>(R.id.navigation_login)
    private val viewModel by activityViewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgetPassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}