package com.cmk.app.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope

/**
 * @Author: romens
 * @Date: 2019-11-5 15:16
 * @Desc:
 */
abstract class BaseFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launchWhenCreated { subscribe() }
        super.onViewCreated(view, savedInstanceState)
    }

    open suspend fun subscribe() {}
}