package com.cmk.app.widget.nestedscroll

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewParent
import androidx.core.view.NestedScrollingChild2
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.ViewParentCompat

class NestedScrollChildView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), NestedScrollingChild2 {

    private lateinit var childHelper: NestedScrollingChildHelper
    private var mView: View? = this
    private var mParentView: ViewParent? = parent

    init {
        childHelper = NestedScrollingChildHelper(this)
    }

    /**
     * 这个方法首先会判断是否已经找到了配合处理滑动的NestedScrollingParent、若找到了则返回true，
     * 否则会判断是否开启嵌套滑动，若开启了则通过构造函数注入的View来循环往上层寻找配合处理滑动的NestedScrollingParent，
     * 循环条件是通过ViewParentCompat这个兼容类判断p是否实现NestedScrollingParent，
     * 若是则将p转为NestedScrollingParent类型调用onStartNestedScroll()方法如果返回true则证明找配合处理滑动的NestedScrollingParent，
     * 所以接下来同样借助ViewParentCompat调用NestedScrollingParent的onNestedScrollAccepted()。
     */
    override fun startNestedScroll(axes: Int, type: Int): Boolean {
//        return childHelper.startNestedScroll(axes, type) // 借助helper处理
        // 判断是否找到配合滑动的nestedscrollingParent
        if (hasNestedScrollingParent()) {
            return true
        }
        // 判断是否开启滑动嵌套
        if (isNestedScrollingEnabled) {
            var parent = this.parent

            // 往上层寻找配合滑动处理的nestedscrollingparent
            while (parent != null) {
                // ViewParentCompat.onStartNestedScroll()会判断parent是否实现NestedScrollingParent，
                // 若是则将p转为NestedScrollingParent类型调用onStartNestedScroll()方法
                if (ViewParentCompat.onStartNestedScroll(parent, this, mView, axes, type)) {
                    //通过ViewParentCompat调用p的onNestedScrollAccepted()方法
                    ViewParentCompat.onNestedScrollAccepted(parent, this, mView, axes, type)
                    return true
                }
                if (parent is View) {
                    mView = parent
                }
                parent = parent.parent
            }
        }
        return false
    }

    /**
     * 这个方法首先会判断是否开启嵌套滑动并找到配合处理滑动的NestedScrollingParent，
     * 若符合这两个条件则会根据参数dx、dy滑动值判断是否有水平或垂直方向滑动，
     * 若有滑动调用mView.getLocationInWindow()将View当前的在Window上的x、y坐标值赋值进offsetInWindow数组并以startX、startY记录，
     * 接下来初始化输出数组consumed、并通过ViewParentCompat调用NestedScrollingParent的onNestedPreScroll()，
     * 再次调用mView.getLocationInWindow()将调用NestedScrollingParent的onNestedPreScroll()后的View在Window上的x、y
     * 坐标值赋值进offsetInWindow数组并与之前记录好的startX、startY相减计算得出偏移量，
     * 接着以consumed数组的两个元素的值有其中一个不为0作为boolean值返回，
     * 若条件为true说明NestedScrollingParent消耗的部分或者全部滑动值。
     */
    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        //如果开启嵌套滑动并找到配合处理滑动的NestedScrollingParent
        if (isNestedScrollingEnabled && mParentView != null) {
            if (dx != 0 || dy != 0) { // 如果有水平方向或垂直方向的滚动
                var startX = 0
                var startY = 0
                offsetInWindow?.let {
                    // 先记录view当前在window上的x，y坐标值
                    val locationInWindow = mView?.getLocationInWindow(it)
                    startX = offsetInWindow[0]
                    startY = offsetInWindow[1]
                }
                // 初始化输出数组
                consumed?.let {
                    it[0] = 0
                    it[1] = 0
                }
                //通过ViewParentCompat调用NestedScrollingParent的onNestedPreScroll()方法
                ViewParentCompat.onNestedPreScroll(mParentView, mView, dx, dy, consumed, type)
                offsetInWindow?.let {
                    //将之前记录好的x、y坐标减去调用NestedScrollingParent的onNestedPreScroll()后View的x、y坐标，
                    // 计算得出偏移量并赋值进offsetInWindow数组
                    mView?.getLocationInWindow(offsetInWindow)
                    offsetInWindow[0] -= startX
                    offsetInWindow[1] -= startY
                }
                return consumed?.let { it[0] != 0 || it[1] != 0 }!!
            } else if (offsetInWindow != null) {
                offsetInWindow[0] = 0
                offsetInWindow[1] = 0
            }
        }
        return false
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        //如果开启嵌套滑动并找到配合处理滑动的NestedScrollingParent
        if (isNestedScrollingEnabled && mParentView != null) {
            //如果有消费滑动值或者有剩余滑动值
            if (dxConsumed != 0 || dyConsumed != 0 || dxUnconsumed != 0 || dyUnconsumed != 0) {
                var startX = 0
                var startY = 0
                offsetInWindow?.let {
                    //先记录View当前的在Window上的x、y坐标值
                    mView?.getLocationInWindow(offsetInWindow)
                    startX = it[0]
                    startY = it[1]
                }
                //通过ViewParentCompat调用NestedScrollingParent的onNestedScroll()方法
                ViewParentCompat.onNestedScroll(
                    mParentView,
                    mView,
                    dxConsumed,
                    dyConsumed,
                    dxUnconsumed,
                    dyUnconsumed,
                    type
                )
                offsetInWindow?.let {
                    //将之前记录好的x、y坐标减去调用NestedScrollingParent的onNestedScroll()后View的x、y坐标，
                    // 计算得出偏移量并赋值进offsetInWindow数组
                    mView?.getLocationInWindow(offsetInWindow)
                    it[0] -= startX
                    it[1] -= startY
                }
                //返回true表明NestedScrollingChild的dispatchNestedScroll事件成功分发NestedScrollingParent
                return true
            } else if (offsetInWindow != null) {
                offsetInWindow[0] = 0
                offsetInWindow[1] = 1
            }
        }
        return false
    }


    override fun stopNestedScroll(type: Int) {
        TODO("Not yet implemented")
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        TODO("Not yet implemented")
    }


}