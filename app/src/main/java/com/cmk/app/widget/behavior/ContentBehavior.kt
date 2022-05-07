package com.cmk.app.widget.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.cmk.app.R

class ContentBehavior @JvmOverloads constructor(context: Context, attrs: AttributeSet) :
    CoordinatorLayout.Behavior<View>(context, attrs) {
    private var statusBarHeight: Int = 0
    private var topBarHeight: Int = 0
    private var contentTransY: Int = 0
    private var downEndY: Int = 0
    private val mLlContent: View? = null //Content部分


    init {
        val resources = context.resources
        val resourcesId = resources.getIdentifier("status_bar_height", "dimen", "android")
        statusBarHeight = resources.getDimensionPixelSize(resourcesId)
        topBarHeight = resources.getDimension(R.dimen.top_bar_height).toInt() + statusBarHeight
        contentTransY = resources.getDimension(R.dimen.content_trans_y).toInt()
        downEndY = resources.getDimension(R.dimen.content_trans_down_end_y).toInt()
    }

    override fun onMeasureChild(
        parent: CoordinatorLayout,
        child: View,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int
    ): Boolean {
        val childHeight = child.layoutParams.height
        if (childHeight == ViewGroup.LayoutParams.MATCH_PARENT || childHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
            //先获取CoordinatorLayout的测量规格信息，若不指定具体高度则使用CoordinatorLayout的高度
            var parentHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec)
            if (parentHeight == 0) {
                parentHeight = parent.height
            }
            //设置Content部分高度
            val height = parentHeight - topBarHeight
            val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                height, if (childHeight == ViewGroup.LayoutParams.MATCH_PARENT)
                    View.MeasureSpec.EXACTLY else View.MeasureSpec.AT_MOST
            )
            //执行指定高度的测量，并返回true表示使用Behavior来代理测量子View
            parent.onMeasureChild(
                child,
                parentWidthMeasureSpec,
                widthUsed,
                heightMeasureSpec,
                heightUsed
            )
            return true
        }

        return false
    }
}