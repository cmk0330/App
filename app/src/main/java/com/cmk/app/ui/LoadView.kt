package com.cmk.app.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.cmk.app.R

@SuppressLint("InflateParams")
class LoadView(context: Context) : View(context) {
    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_loading, null)
    }
}