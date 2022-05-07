package com.cmk.app.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * @Author: romens
 * @Date: 2019-11-11 16:57
 * @Desc:
 */
abstract class BaseDBActivity<DB : ViewDataBinding> : AppCompatActivity() {

    inline fun <reified T : ViewDataBinding> binding(
        @LayoutRes resId: Int
    ): Lazy<T> = lazy { DataBindingUtil.setContentView<T>(this, resId) }

    abstract val layoutId: Int
    lateinit var binding: DB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
        subscribe()
    }

//    private fun initViewModel() {
//        providerViewModelClass()?.let {
//            viewModel = ViewModelProvider(this).get(it)
//            lifecycle.addObserver(viewModel)
//        }
//    }

    open fun subscribe() {}

    @MainThread
    inline fun <reified DB : ViewDataBinding> dataBinding1(@LayoutRes layoutId: Int): Lazy<DB> =
        lazy { DataBindingUtil.setContentView<DB>(this, layoutId) }




//    open fun providerViewModelClass(): Class<VM>? = null
}