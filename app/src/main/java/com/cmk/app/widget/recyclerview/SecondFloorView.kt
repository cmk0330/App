package com.cmk.app.widget.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import com.cmk.app.R
import kotlinx.android.synthetic.main.layout_second_floor_view.view.*

/**
 * @Author: romens
 * @Date: 2020-1-14 9:59
 * @Desc:
 */
class SecondFloorView(context: Context?) : FrameLayout(context!!) {

    private var callback: (() -> Unit)? = null
    private var ivShow: ImageView? = null

    init {
        val view =
            LayoutInflater.from(context).inflate(R.layout.layout_second_floor_view, this, false)
        addView(view)
        text.setOnClickListener {
            callback?.invoke()
        }
    }

    fun setOnClickCallback(callback: (() -> Unit)) {
        this.callback = callback
    }

    interface OnClickCallback {
        fun callback()
    }
}