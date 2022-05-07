package com.cmk.app.ui

import com.cmk.app.ui.dialog.LoadingDialog
import com.cmk.app.util.lifecycle.ActivityStackManager

/**
 * @Author: romens
 * @Date: 2019-12-16 14:08
 * @Desc:
 */

val Any.stateDialog by lazy {
    ActivityStackManager.currentActivity?.let { LoadingDialog(it) }
}

fun Any.show() {
    stateDialog?.show()
}

fun Any.dismiss() {
    stateDialog?.dismiss()
}