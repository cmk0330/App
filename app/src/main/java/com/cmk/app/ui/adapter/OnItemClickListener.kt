package com.cmk.app.ui.adapter

import android.view.View

/**
 * @Author: romens
 * @Date: 2019-11-12 9:00
 * @Desc:
 */
interface OnItemClickListener<T> {

    fun onItemClick(view:View,  t:T)
}