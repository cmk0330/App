package com.cmk.app.ui.adapter

import android.view.View

/**
 * @Author: romens
 * @Date: 2019-12-18 15:54
 * @Desc:
 */
interface OnItemViewClickListener<T> {
    fun onItemViewClick(view:View, t:T)
}