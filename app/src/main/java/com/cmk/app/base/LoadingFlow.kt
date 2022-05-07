package com.cmk.app.base

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.ContentFrameLayout
import com.cmk.app.R
import com.cmk.app.util.lifecycle.ActivityStackManager
import com.cmk.app.widget.StatusLayout
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.parameter.emptyParametersHolder

class LoadingFlow {
}

sealed class ViewStatus() {


    data class Loading(val loadingView: View) : ViewStatus() {}

//    data class Success(val view: View) : ViewStatus() {}

    data class Failure(val view: View) : ViewStatus() {}
}

@SuppressLint("InflateParams")
fun doViewStatus(status: ViewStatus) {

//    val loadingView =
//        LayoutInflater.from(statusView?.context).inflate(R.layout.layout_loading, null)
//    val failureView =
//        LayoutInflater.from(statusView?.context).inflate(R.layout.layout_failure, null)
//
//    val statusViewFlow = MutableStateFlow<ViewStatus>(ViewStatus.Failure(statusView!!))
//    when (status) {
//        is ViewStatus.Loading -> {
//            statusView?.addView(loadingView, 0)
//            statusViewFlow.value = ViewStatus.Loading(loadingView)
//        }
//        is ViewStatus.Failure -> {
//            statusView?.removeView(loadingView)
//            statusView?.addView(failureView, 0)
//            statusViewFlow.value = ViewStatus.Failure(failureView)
//        }
////        is ViewStatus.Success -> statusViewFlow.value = ViewStatus.Success(view)
//    }
}

val statusLayout by lazy {
    val contentView =
        ActivityStackManager.currentActivity?.findViewById<ContentFrameLayout>(android.R.id.content)
    val layout = StatusLayout(contentView!!.context)
    contentView.addView(layout)
    layout
}

inline fun ViewStatus.doLoading(view: (View) -> Unit) {
    if (this is ViewStatus.Loading) view(loadingView)
}