package com.cmk.app.base

import android.util.Log
import androidx.lifecycle.Observer

/**
 * @Author: romens
 * @Date: 2019-11-28 14:22
 * @Desc:
 */
class ExceptionObserver :Observer<Throwable>{
    override fun onChanged(t: Throwable?) {
        Log.e("ExceptionObserver", "${t?.message}")
    }
}