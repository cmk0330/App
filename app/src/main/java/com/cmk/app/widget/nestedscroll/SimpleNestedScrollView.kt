package com.cmk.app.widget.nestedscroll

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

class SimpleNestedScrollView(context: Context, attrs: AttributeSet) : ScrollView(context, attrs) {
    private var isFirstIntercept = true
    private var isNeedRequestDisallowIntercept: Boolean? = null

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (ev.actionMasked == MotionEvent.ACTION_DOWN) isFirstIntercept = true

        val result = super.onInterceptTouchEvent(ev)
        if (result && isFirstIntercept) {
            isFirstIntercept = false
            return false
        }
        return result
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.actionMasked == MotionEvent.ACTION_DOWN) isNeedRequestDisallowIntercept = null
        if (ev.actionMasked == MotionEvent.ACTION_MOVE) {
            if (isNeedRequestDisallowIntercept == false) return false
            if (isNeedRequestDisallowIntercept == null) {
//                val offsetY = ev.y.toInt() - getInt("mLastMotionY")
            }
        }
        return super.onTouchEvent(ev)
    }
}