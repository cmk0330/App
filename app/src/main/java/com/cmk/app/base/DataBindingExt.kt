package com.cmk.app.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding


@MainThread
inline fun <reified DB : ViewDataBinding> FragmentActivity.testDataBinding(
    @LayoutRes layoutId: Int
): Lazy<DB> =
    lazy { DataBindingUtil.setContentView<DB>(this, layoutId) }

@MainThread
inline fun <reified DB : ViewDataBinding> Fragment.testDataBinding(
    inflater: LayoutInflater,
    @LayoutRes layoutId: Int,
    container: ViewGroup?
): Lazy<DB> =
    lazy { DataBindingUtil.inflate<DB>(inflater, layoutId, container, false) }


/**
 * 用于延迟加载databinding Activity
 */
@MainThread
inline fun <reified DB : ViewDataBinding> FragmentActivity.dataBinding(@LayoutRes layoutId: Int)
        : ActivityDataBindingLazy<DB> = ActivityDataBindingLazy(this, layoutId)

/**
 * 用于延迟加载databinding Fragment
 */
@MainThread
inline fun <reified DB : ViewDataBinding> Fragment.dataBinding(
    inflater: LayoutInflater,
    @LayoutRes layoutId: Int,
    container: ViewGroup?
): FragmentDataBindingLazy<DB> = FragmentDataBindingLazy(this, inflater, layoutId, container)

/**
 *  adapter
 */
@MainThread
inline fun <reified DB : ViewDataBinding> dataBinding(
    inflater: LayoutInflater,
    @LayoutRes layoutId: Int,
    container: ViewGroup?
): Lazy<DB> = lazy { DataBindingUtil.inflate<DB>(inflater, layoutId, container, false) }

/**
 * 延迟初始化
 */
class ActivityDataBindingLazy<DB : ViewDataBinding>(
    private val activity: FragmentActivity,
    @LayoutRes private val layoutId: Int
) : Lazy<DB> {
    private var cached: DB? = null
    override val value: DB
        get() {
            val dataBinding = cached
            return dataBinding
                ?: DataBindingUtil.setContentView<DB>(activity, layoutId).also {
                    cached = it
                    it.lifecycleOwner = activity
                }
        }

    override fun isInitialized(): Boolean = cached != null
}

/**
 *
 */
class FragmentDataBindingLazy<DB : ViewDataBinding>(
    private val fragment: Fragment,
    private val inflater: LayoutInflater,
    @LayoutRes private val layoutId: Int,
    private val container: ViewGroup?
) : Lazy<DB> {
    private var cached: DB? = null
    override val value: DB
        get() {
            val dataBinding = cached
            return dataBinding
                ?: DataBindingUtil.inflate<DB>(inflater, layoutId, container, false)
                    .also {
                        cached = it
                        it.lifecycleOwner = fragment
                    }
        }

    override fun isInitialized(): Boolean = cached != null
}
