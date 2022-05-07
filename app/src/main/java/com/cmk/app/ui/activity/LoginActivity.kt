package com.cmk.app.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.cmk.app.R
import com.cmk.app.base.BaseActivity
import com.cmk.app.compose.TimeUtil
import com.cmk.app.databinding.ActivityLoginBinding
import com.cmk.app.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collect

/**
 * @Author: romens
 * @Date: 2019-11-13 16:52
 * @Desc:
 */
class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    /**
     * 沉浸式
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.fragment_login_regedit_state).navigateUp()
    }

    override suspend fun subscribe() {
        super.subscribe()
        viewModel.loginState.collect {
            if (it.loginState) {
                this.finish()
            } else {
                Toast.makeText(this, it.errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}