package com.cmk.app.base

import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.Observer

/**
 * @Author: romens
 * @Date: 2019-11-11 14:53
 * @Desc:
 */
class LoadingObserver(var context: Context?) : Observer<Boolean> {

    val progressBar: ProgressDialog = ProgressDialog(context)

    override fun onChanged(t: Boolean?) {
        Log.e("LoadingObserver-->", "$t")
        if (t!!) progressBar.show() else progressBar.dismiss()
    }
}