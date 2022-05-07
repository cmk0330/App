package com.cmk.app.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmk.app.ext.LoadingViewState
import com.cmk.app.ext.MyToast
import com.cmk.app.ui.dialog.LoadingDialog
import com.cmk.app.util.lifecycle.ActivityStackManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * @Author: romens
 * @Date: 2019-11-5 15:16
 * @Desc:
 */
open class BaseViewModel : ViewModel() {

    private val stateDialog by lazy {
        //        ProgressBar(ActivityManager.currentActivity)
        ActivityStackManager.currentActivity?.let { LoadingDialog(it) }
    }

    fun loadingState(isLoading: Boolean = false, msg: String? = null) {
        msg?.let { MyToast(App.context, it) }
        if (isLoading) stateDialog?.show() else stateDialog?.dismiss()
    }

    inline fun launch(crossinline block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch { block() }
    }
}