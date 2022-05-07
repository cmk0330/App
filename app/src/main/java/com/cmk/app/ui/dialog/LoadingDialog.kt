package com.cmk.app.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.cmk.app.R

/**
 * @Author: romens
 * @Date: 2020-1-17 9:31
 * @Desc:
 */
class LoadingDialog(context: Context) : AlertDialog(context, R.style.LoadingDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null)
        setContentView(view)
        setCanceledOnTouchOutside(false)
    }
}