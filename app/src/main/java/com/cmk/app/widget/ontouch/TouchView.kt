package com.cmk.app.widget.ontouch

import android.view.MotionEvent

/**
 * 如果View判断自己要消费事件，而且执行的是不希望被父View打断的操作时，
 * 需要立刻调用父View的requestDisallowInterceptTouchEvent()方法
 */
interface ViewParent {
    fun requestDisallowInterceptTouchEvent(isDisallowIntercept: Boolean)
}

/**
 * 相当于层级view里面的childView
 */
open class TouchView {

    var parent: ViewParent? = null
    open fun passEvent(ev: MotionEvent) {}

    /**
     *  向里传递事件并只传递事件
     *  open fun passIn(ev: MotionEvent) {
     *  passOut(ev)
     * }
     */
    //


    /**
     *  向外传递事件，并且传递过程中每个view是否决定处理事件，处理后不再调用parent的passout()
     *  open fun passOut(ev: MotionEvent) {
     *  parent?.passOut(ev)
     * }
     */

    // 单独放事件传递的控制逻辑
    open fun dispatch(ev: MotionEvent): Boolean {
        return onTouch(ev)
    }

    // 返回值表示是否 处理的事件
    open fun onTouch(ev: MotionEvent): Boolean {
        return false
    }

}

/**
 * 相当于parentView
 */
class TouchGroupView(private val child: TouchView) : TouchView(), ViewParent {

    // 对是子View是否处理了DOWN事件进行记录
    // 在收到DOWN事件的最开始和收到UP事件的最后，重置状态
    private var isChildNeedEvent = false

    // 父view是否自己处理事件
    private var isSelfNeedEvent = false

    // 子view是否请求驳回事件拦截处理
    private var isDisallowIntercept = false


    init {
        child.parent = this // 这里只是示意，实际中不建议这么写，会造成提前发布未构造完成的实例
    }

    override fun passEvent(ev: MotionEvent) {
        child.passEvent(ev)
    }

//    override fun passIn(ev: MotionEvent) {
//        child.passIn(ev)
//    }

    override fun dispatch(ev: MotionEvent): Boolean {
        var handled = false // 子view是否处理事件
        if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
            clearStatus()
            if (!isDisallowIntercept && onIntercept(ev)) { // 事件被拦截，并且子view没有请求驳回拦截，表示父view自己处理事件
                isSelfNeedEvent = true
                handled = onTouch(ev)
            } else { // 事件交给子view处理
                handled = child.dispatch(ev)
                if (handled) isChildNeedEvent = true // 子view处理事件
                if (!handled) { // 子view并不需要消费事件
                    handled = onTouch(ev)
                    if (handled) isSelfNeedEvent = true // 父view自己处理掉
                }
            }
        } else {
            // 这里 isSelfNeedEvent 条件判断应该放在 isChildNeedEvent 前面
            // 因为两个都为真的情况只能是自己之后通过 onIntercept 抢了控制权，那这之后的控制权就不会去 child 那儿了
            if (isSelfNeedEvent) {
                handled = onTouch(ev)
            } else if (isChildNeedEvent) {
                if (!isDisallowIntercept && onIntercept(ev)) { // 事件被拦截，并且子view没有请求驳回拦截，表示父view自己处理事件
                    isSelfNeedEvent = true
                    val cancel = MotionEvent.obtain(ev) // 给子view分发个cancel事件，强制取消
                    cancel.action = MotionEvent.ACTION_CANCEL
                    handled = child.dispatch(cancel)
                    cancel.recycle()
                } else { // 事件交给子view处理
                    handled = child.dispatch(ev)
                }
            }
        }

        if (ev.actionMasked == MotionEvent.ACTION_UP ||
            ev.actionMasked == MotionEvent.ACTION_CANCEL
        ) {
            clearStatus()
        }
        return handled
    }

    override fun onTouch(ev: MotionEvent): Boolean {
        return false
    }

    private fun clearStatus() {
        isChildNeedEvent = false
        isSelfNeedEvent = false
        isDisallowIntercept = false
    }

    open fun onIntercept(ev: MotionEvent): Boolean {
        return false
    }

    override fun requestDisallowInterceptTouchEvent(isDisallowIntercept: Boolean) {
        this.isDisallowIntercept = isDisallowIntercept
        parent?.requestDisallowInterceptTouchEvent(isDisallowIntercept)
    }
}
