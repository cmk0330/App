package com.cmk.app.ext

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.cmk.app.R
import com.cmk.app.ui.dialog.LoadingDialog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Created by luyao
 * on 2019/5/31 16:42
 */


fun Context.toast(content: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, content, duration).apply {
        show()
    }
}

fun Context.toast(@StringRes id: Int, duration: Int = Toast.LENGTH_SHORT) {
    toast(getString(id), duration)
}

fun Context.longToast(content: String) {
    toast(content, Toast.LENGTH_LONG)
}

fun Context.longToast(@StringRes id: Int) {
    toast(id, Toast.LENGTH_LONG)
}

fun Any.toast(context: Context, content: String, duration: Int = Toast.LENGTH_SHORT) {
    context.toast(content, duration)
}

fun Any.toast(context: Context, @StringRes id: Int, duration: Int = Toast.LENGTH_SHORT) {
    context.toast(id, duration)
}


fun Any.longToast(context: Context, content: String) {
    context.longToast(content)
}

fun Any.longToast(context: Context, @StringRes id: Int) {
    context.longToast(id)
}

fun Any.MyToast(context: Context, content: String) {
    val toast = Toast(context)
    toast.setGravity(Gravity.CENTER, 0, 0)
    val view = LayoutInflater.from(context).inflate(R.layout.toast_ext, null)
    val tvMessage = view.findViewById<TextView>(R.id.tv_message)
    tvMessage.text = content
    toast.view = view
    toast.show()
}

fun  Context.loadDialog(): StateFlow<LoadingDialog> {
    val loadingDialog = LoadingDialog(this)
    val loadingStateFlow = MutableStateFlow(LoadingDialog(this))
    return loadingStateFlow.asStateFlow()
}


