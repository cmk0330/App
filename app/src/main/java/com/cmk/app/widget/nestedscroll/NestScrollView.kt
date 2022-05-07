package com.cmk.app.widget.nestedscroll

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.NestedScrollingChild
import androidx.core.view.NestedScrollingParent

/**
 * https://juejin.im/post/5ede31496fb9a047a226a44a#heading-8
 */
class NestScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), NestedScrollingParent, NestedScrollingChild {

    /*-----------------------------------------parent--------------------------------------------*/

    /**
     * 对NestedScrollingChild发起嵌套滑动作出应答
     * @param child 布局中包含下面target的直接父View
     * @param target 发起嵌套滑动的NestedScrollingChild的View
     * @param axes 滑动方向
     * @return 返回NestedScrollingParent是否配合处理嵌套滑动
     */
    override fun onStartNestedScroll(child: View, target: View, axes: Int): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * NestedScrollingParent配合处理嵌套滑动回调此方法
     * @param child 布局中包含下面target的直接父View
     * @param target 发起嵌套滑动的NestedScrollingChild的View
     * @param axes 滑动方向
     */
    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        TODO("Not yet implemented")
    }

    /**
     * 嵌套滑动结束
     * @param target 发起嵌套滑动的NestedScrollingChild的View
     */
    override fun onStopNestedScroll(target: View) {
        TODO("Not yet implemented")
    }

    /**
     * NestedScrollingChild滑动完之前将滑动值分发给NestedScrollingParent回调此方法
     * @param target 同上
     * @param dx 水平方向的距离
     * @param dy 水平方向的距离
     * @param consumed 返回NestedScrollingParent是否消费部分或全部滑动值
     */
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        TODO("Not yet implemented")
    }

    /**
     * NestedScrollingChild滑动完成后将滑动值分发给NestedScrollingParent回调此方法
     * @param target 同上
     * @param dxConsumed 水平方向消费的距离
     * @param dyConsumed 垂直方向消费的距离
     * @param dxUnconsumed 水平方向剩余的距离
     * @param dyUnconsumed 垂直方向剩余的距离
     */
    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        TODO("Not yet implemented")
    }

    /**
     * NestedScrollingChild在惯性滑动之前,将惯性滑动的速度分发给NestedScrollingParent
     * @param target 同上
     * @param velocityX 同上
     * @param velocityY 同上
     * @return 返回NestedScrollingParent是否消费全部惯性滑动
     */
    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * NestedScrollingChild在惯性滑动之前，将惯性滑动的速度和NestedScrollingChild自身是否需要消费此惯性滑动分
     * 发给NestedScrollingParent回调此方法
     * @param target 同上
     * @param velocityX 水平方向的速度
     * @param velocityY 垂直方向的速度
     * @param consumed NestedScrollingChild自身是否需要消费此惯性滑动
     * @return 返回NestedScrollingParent是否消费全部惯性滑动
     */
    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * @return 返回当前嵌套滑动的方向
     */
    override fun getNestedScrollAxes(): Int {
        TODO("Not yet implemented")
    }

    /*-----------------------------------------child--------------------------------------------*/

    /**
     * @param enabled 开启或关闭嵌套滑动
     */
    override fun setNestedScrollingEnabled(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    /**
     * @return 返回是否开启嵌套滑动
     */
    override fun isNestedScrollingEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * 沿着指定的方向开始滑动嵌套滑动
     * @param axes 滑动方向
     * @return 返回是否找到NestedScrollingParent配合滑动
     */
    override fun startNestedScroll(axes: Int): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * 停止嵌套滑动
     */
    override fun stopNestedScroll() {
        TODO("Not yet implemented")
    }

    /**
     * @return 返回是否有配合滑动NestedScrollingParent
     */
    override fun hasNestedScrollingParent(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * 在滑动之前，将滑动值分发给NestedScrollingParent
     * @param dx 水平方向消费的距离
     * @param dy 垂直方向消费的距离
     * @param consumed 输出坐标数组，consumed[0]为NestedScrollingParent消耗的水平距离、
     * consumed[1]为NestedScrollingParent消耗的垂直距离，此参数可空。
     * @param offsetInWindow 同上dispatchNestedScroll
     * @return 返回NestedScrollingParent是否消费部分或全部滑动值
     */
    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?
    ): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * 滑动完成后，将已经消费、剩余的滑动值分发给NestedScrollingParent
     * @param dxConsumed 水平方向消费的距离
     * @param dyConsumed 垂直方向消费的距离
     * @param dxUnconsumed 水平方向剩余的距离
     * @param dyUnconsumed 垂直方向剩余的距离
     * @param offsetInWindow 含有View从此方法调用之前到调用完成后的屏幕坐标偏移量，
     * 可以使用这个偏移量来调整预期的输入坐标（即上面4个消费、剩余的距离）跟踪，此参数可空。
     * @return 返回该事件是否被成功分发
     */
    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?
    ): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * 在惯性滑动之前，将惯性滑动值分发给NestedScrollingParent
     * @param velocityX 水平方向的速度
     * @param velocityY 垂直方向的速度
     * @return 返回NestedScrollingParent是否消费全部惯性滑动
     */
    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * 将惯性滑动的速度和NestedScrollingChild自身是否需要消费此惯性滑动分发给NestedScrollingParent
     * @param velocityX 水平方向的速度
     * @param velocityY 垂直方向的速度
     * @param consumed NestedScrollingChild自身是否需要消费此惯性滑动
     * @return 返回NestedScrollingParent是否消费全部惯性滑动
     */
    override fun dispatchNestedFling(
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        TODO("Not yet implemented")
    }

}