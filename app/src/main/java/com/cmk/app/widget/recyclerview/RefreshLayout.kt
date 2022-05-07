package com.cmk.app.widget.recyclerview

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ListView
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.core.widget.ListViewCompat
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * Created by cuimingkun on 2019/11/21. 增加了多点触控
 * https://github.com/zhangxq/QRefreshLayout
 */
class RefreshLayout @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    ViewGroup(context, attrs), NestedScrollingParent, NestedScrollingChild {

    private var viewTarget: View? = null // 刷新目标

    //绘制view相关参数
    private val mRefreshViewIndex = -1
    private var mActivePointerId = 0
    private val mNestedScrollingParentHelper: NestedScrollingParentHelper
    private val mNestedScrollingChildHelper: NestedScrollingChildHelper

    // 滑动事件相关参数
    private var lastMoveY = 0f // 上次移动到的位置y
    private var overScroll = 0f // 上拉和下拉的距离
    private var dragMode = 0 // 拖动模式
    private val dragRate = 0.5f // 滑动速率

    // 刷新逻辑控制参数
    private var mReturningToStart = false
    private var isRefreshing = false // 是否正在进行刷新动画
    private var isLoading = false // 是否正在进行加载更多动画
    private var isAnimating = false // 是否正在进行状态切换动画
    private var isLoadEnable = false // 是否可以加载更多
    private var isAutoLoad = false // 是否打开自动加载更多
    private var isTouchDown = false // 手指是否按下
    private var isPullingUp = false// 是否手指上滑

    /**
     * 当前是否在二楼
     */
    private var isSecondFloor = false // 是否正在二楼
    private var isCanSecondFloor = false // 是否存在二楼
    private val viewRefreshContainer: RelativeLayout // 下拉刷新view容器
    private val viewLoadContainer: RelativeLayout // 加载更多view容器
    private var viewRefresh: RefreshView // 下拉刷新view
    private var viewLoad: LoadView // 加载更多view
    private var viewContentHeight = 2000 // 刷新动画内容区高度
    private var refreshMidHeight = 160 // 刷新高度，超过这个高度，松手即可刷新
    private var loadMidHeight = 128 // 加载更多高度，超过这个高度，松手即可加载更多
    private var secondFloorHeight = 256 // 二楼高度，超过这个高度，松手即可到达二楼
    private var refreshHeight = 128 // 刷新动画高度
    private var loadHeight = 128 // 加载更多动画高度
    private val animateDuration = 200 // 动画时间ms

    // nested 相关参数
    private var nestedOverScroll = 0f
    private var isNestedScrolling = false
    private val mParentScrollConsumed = IntArray(2)
    private val mParentOffsetInWindow = IntArray(2)

    // 动画
    private var animatorToRefresh: ValueAnimator? = null // 移动到刷新位置
    private var animatorToRefreshReset: ValueAnimator? = null // 移动到刷新初始位置
    private var animatorToLoad: ValueAnimator? = null // 移动到加载更多位置
    private var animatorToLoadReset: ValueAnimator? = null // 移动到加载更多初始位置
    private var animatorToSecondFloor: ValueAnimator? = null // 移动到二楼
    private var animatorToFirstFloor: ValueAnimator? = null // 移动到一楼

    private var refreshListener: (() -> Unit)? = null
    private var loadListener: (() -> Unit)? = null
    private var listScrollListener: ListScrollListener? = null

    private var mChildScrollUpCallback: ((parent: RefreshLayout, child: View?) -> Boolean)? = null
//    private var mChildScrollUpCallback: OnChildScrollUpCallback? = null

    companion object {
        private val TAG = RefreshLayout::class.java.simpleName
        private const val INVALID_INDEX = -1
        private const val INVALID_POINTER = -1 //
        private const val N_DOWN = 1 //正常下拉
        private const val N_UP = 2 // 正常上拉
        private const val R_UP = 3 // 刷新中上拉
        private const val L_DOWN = 4 // 加载中下拉
    }

    init {
        setWillNotDraw(false)
        viewRefresh = DefaultRefreshView(context)
        viewLoad = DefaultLoadView(context)
        viewRefreshContainer = RelativeLayout(context)
        viewRefreshContainer.gravity = Gravity.CENTER
        viewRefreshContainer.addView(viewRefresh)
        viewLoadContainer = RelativeLayout(context)
        viewLoadContainer.gravity = Gravity.CENTER
        viewLoadContainer.addView(viewLoad)
        viewLoadContainer.visibility = View.GONE
        addView(viewRefreshContainer)
        addView(viewLoadContainer)
        isChildrenDrawingOrderEnabled = true
        mNestedScrollingParentHelper = NestedScrollingParentHelper(this)
        mNestedScrollingChildHelper = NestedScrollingChildHelper(this)
        isNestedScrollingEnabled = true
        //        ensureTarget();
    }

    /**
     * 设置下拉到"释放即可更新"的高度（默认160px）
     */
    fun setPullToRefreshHeight(height: Int) {
        refreshMidHeight = height
    }

    /**
     * 设置上拉到"释放即可加载更多"的高度（默认160px）
     */
    fun setLoadToRefreshHeight(height: Int) {
        loadMidHeight = height
    }

    /**
     * 设置下拉刷新动画高度（默认150px，需要在setRefreshing之前调用）
     */
    fun setRefreshHeight(height: Int) {
        refreshHeight = height
    }

    /**
     * 设置加载更多动画高度（默认110px）
     */
    fun setLoadHeight(height: Int) {
        loadHeight = height
    }

    /**
     * 设置下拉到"释放到达二楼"的高度（默认500px）
     */
    fun setPullToSecondFloorHeight(height: Int) {
        secondFloorHeight = height
    }

    /**
     * 回到一楼
     */
    fun setBackToFirstFloor() {
        animateToFirstFloor()
    }

    /**
     * 设置是否可以到达二楼
     */
    fun setIsCanSecondFloor(isCanSecondFloor: Boolean) {
        this.isCanSecondFloor = isCanSecondFloor
    }

    /**
     * 设置二楼view，仅限于使用默认header
     */
    fun setSecondFloorView(secondFloorView: View?) {
        if (viewRefresh is DefaultRefreshView) {
            (viewRefresh as DefaultRefreshView).setSecondFloorView(secondFloorView)
        } else {
            Log.d(
                "QRefreshLayout",
                "no DefaultRefreshView, please set secondFloorView by yourself"
            )
        }
    }

    /**
     * 设置是否可以加载更多
     */
    fun setLoadEnable(isEnable: Boolean) {
        isLoadEnable = isEnable
        if (isLoadEnable) viewLoadContainer.visibility = View.VISIBLE
        else viewLoadContainer.visibility = View.GONE
    }

    /**
     * 设置自动加载更多开关，默认开启
     */
    fun setAutoLoad(isAutoLoad: Boolean) {
        this.isAutoLoad = isAutoLoad
    }

    /**
     * 设置下拉刷新view
     */
    fun setRefreshView(refreshView: RefreshView) {
        viewRefresh = refreshView
        viewRefreshContainer.removeAllViews()
        viewRefreshContainer.addView(viewRefresh)
    }

    /**
     * 设置加载更多view
     */
    fun setLoadView(loadView: LoadView) {
        viewLoad = loadView
        viewLoadContainer.removeAllViews()
        viewLoadContainer.addView(viewLoad)
    }

    /**
     * 如果使用了默认加载动画，设置进度圈颜色资源
     */
    fun setColorSchemeResources(@ColorRes vararg colorResIds: Int) {
        val context = context
        val colorRes = IntArray(colorResIds.size)
        for (i in colorResIds.indices)
            colorRes[i] = ContextCompat.getColor(context, colorResIds[i])

        setColorSchemeColors(*colorRes)
    }

    /**
     * 如果使用了默认加载动画，设置进度圈颜色
     */
    fun setColorSchemeColors(@ColorInt vararg colors: Int) {
        if (viewRefresh is DefaultRefreshView)
            (viewRefresh as DefaultRefreshView).setColorSchemeColors(*colors)

    }

    /**
     * Set the background color of the progress spinner disc.
     * @param colorRes Resource id of the color.
     */
    fun setProgressBackgroundColorSchemeResource(@ColorRes colorRes: Int) {
        setProgressBackgroundColorSchemeColor(ContextCompat.getColor(context, colorRes))
    }

    /**
     * Set the background color of the progress spinner disc.
     * @param color
     */
    fun setProgressBackgroundColorSchemeColor(@ColorInt color: Int) {
        if (viewRefresh is DefaultRefreshView)
            viewRefresh.setBackgroundColor(color)
    }

    /**
     * 设置下拉刷新监听
     */
    fun setOnRefreshListener(listener: () -> Unit) {
        refreshListener = listener
    }

    /**
     * 设置加载更多监听
     */
    fun setOnLoadListener(listener: () -> Unit) {
        loadListener = listener
        isLoadEnable = true
        setAutoLoad(true)
        viewLoadContainer.visibility = View.VISIBLE
    }

    /**
     * 设置是否显示正在刷新
     */
    fun setRefreshing(refreshing: Boolean) {
        ensureTarget()
        if (refreshing) {
            if (isRefreshing || isLoading || dragMode != 0) return
            if (!isRefreshing)
                animateToRefresh()
        } else {
            isRefreshing = false
            if (overScroll >= 0)
                animateToRefreshReset()
        }
    }

    /**
     * 获取是否正在刷新
     */
    fun isRefreshing() = isRefreshing

    /**
     * 设置是否显示正在加载更多
     */
    fun setLoading(loading: Boolean) {
        if (!isLoadEnable) return
        ensureTarget()
        if (loading) {
            if (isLoading || isRefreshing || dragMode != 0) return
            if (!isLoading)
                animateToLoad()
        } else {
            isLoading = false
            if (overScroll <= 0)
                animateToLoadReset()
        }
    }

    /**
     * 获取是否加载更多
     */
    fun isLoading(): Boolean = isLoading

    /**
     * 设置ListView滚动监听
     */
    fun setListViewScrollListener(listener: ListScrollListener?) {
        listScrollListener = listener
    }

    /**
     * childView的绘制顺序:先绘制刷新头部
     */
    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        return when {
            mRefreshViewIndex < 0 -> i
            i == 0 -> mRefreshViewIndex // 首先绘制选中的子view
            i <= mRefreshViewIndex -> i - 1  // 将子项移到选定子项之前
            else -> i
        }

//        if (mRefreshViewIndex < 0) {
//            return i;
//        } else if (i == childCount - 1) {
//            // Draw the selected child last 这是最后一个需要刷新的item
//            return mRefreshViewIndex;
//        } else if (i >= mRefreshViewIndex) {
//            // Move the children after the selected child earlier one
//            return i + 1;
//        } else {
//            // Keep the children before the selected child the same
//            return i;
//        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = measuredWidth
        val height = measuredHeight
        viewContentHeight = height
        if (childCount == 0)
            return
        ensureTarget()
        if (viewTarget == null)
            return
        val child: View = viewTarget!!
        if (child.background == null)
            child.setBackgroundColor(-0x1)
        else
            child.background.alpha = 255
        val childLeft = paddingLeft
        val childTop = paddingTop
        val childWidth = width - paddingLeft - paddingRight
        val childHeight = height - paddingTop - paddingBottom
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
        viewRefreshContainer.layout(0, -viewContentHeight / 2, width, viewContentHeight / 2)
        viewLoadContainer.layout(
            0,
            height - viewContentHeight / 2,
            width,
            height + viewContentHeight / 2
        )
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewContentHeight = measuredHeight
        ensureTarget()
        if (viewTarget == null)
            return
        viewTarget!!.measure(
            MeasureSpec.makeMeasureSpec(
                measuredWidth - paddingLeft - paddingRight,
                MeasureSpec.EXACTLY
            ),
            MeasureSpec.makeMeasureSpec(
                measuredHeight - paddingTop - paddingBottom,
                MeasureSpec.EXACTLY
            )
        )
        viewRefreshContainer.measure(
            MeasureSpec.makeMeasureSpec(
                measuredWidth - paddingLeft - paddingRight,
                MeasureSpec.EXACTLY
            ),
            MeasureSpec.makeMeasureSpec(viewContentHeight, MeasureSpec.EXACTLY)
        )
        viewLoadContainer.measure(
            MeasureSpec.makeMeasureSpec(
                measuredWidth - paddingLeft - paddingRight,
                MeasureSpec.EXACTLY
            ),
            MeasureSpec.makeMeasureSpec(viewContentHeight, MeasureSpec.EXACTLY)
        )
    }

    private fun onScroll() {
        if (!canChildScrollUp() && isAutoLoad && isLoadEnable && !isLoading && isPullingUp && !isTouchDown)
            animateToLoad()
    }

    private fun ensureTarget() {
        if (viewTarget == null) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child != viewRefreshContainer && child != viewLoadContainer) {
                    viewTarget = child
                    viewTarget!!.isClickable = true
                    //                    setScrollListener(); 加载更多监听
                    break
                }
            }
        }
    }

    private fun setScrollListener() {
        if (viewTarget is ListView) {
            (viewTarget as ListView).setOnScrollListener(object : AbsListView.OnScrollListener {
                override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                    if (listScrollListener != null) {
                        listScrollListener!!.onScrollStateChanged(view, scrollState)
                    }
                }

                override fun onScroll(
                    view: AbsListView,
                    firstVisibleItem: Int,
                    visibleItemCount: Int,
                    totalItemCount: Int
                ) {
                    if (listScrollListener != null) {
                        listScrollListener!!.onScroll(
                            view,
                            firstVisibleItem,
                            visibleItemCount,
                            totalItemCount
                        )
                    }
                    onScroll()
                }
            })
        }
        if (viewTarget is RecyclerView) {
            (viewTarget as RecyclerView).addOnScrollListener(object :
                RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    onScroll()
                }
            })
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked
        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false
        }
        val pointerIndex: Int
        if (!isEnabled || mReturningToStart || isAnimating || isNestedScrolling || isSecondFloor) {
            return false
        }
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = ev.getPointerId(0)
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) return false
                isTouchDown = true
                lastMoveY = ev.getY(pointerIndex)
                dragMode = 0
            }
            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(TAG, "执行ACTION_MOVE事件，但没有活动指针id.")
                    return false
                }
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0)
                    return false
                val y = ev.getY(pointerIndex)
                val yDiff = y - lastMoveY
                if (yDiff == 0f) return false
                if (yDiff < 0) isPullingUp = true
                if (yDiff > 0) { // 下拉
                    if (overScroll < 0 && isLoading) {
                        dragMode = L_DOWN
                    } else if (!canChildScrollDown()) {
                        dragMode = N_DOWN
                    }
                } else { // 上拉
                    if (overScroll > 0 && isRefreshing) {
                        dragMode = R_UP
                    } else if (!canChildScrollUp()) {
                        if (isLoadEnable) {
                            dragMode = N_UP
                        }
                    }
                }
                if (dragMode != 0) {
                    lastMoveY = ev.getY(pointerIndex)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isTouchDown = false
                mActivePointerId = INVALID_POINTER
            }
        }
        return dragMode != 0
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        val pointerIndex: Int
        if (mReturningToStart && action == MotionEvent.ACTION_DOWN)
            mReturningToStart = false
        if (!isEnabled || mReturningToStart || isAnimating || isNestedScrolling || isSecondFloor)
            return false
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = event.getPointerId(0)
                dragMode = 0
            }
            MotionEvent.ACTION_MOVE -> {
                pointerIndex = event.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(TAG, "执行ACTION_MOVE事件，但没有活动指针id.")
                    return false
                }
                val y = event.getY(pointerIndex)
                overScroll += (y - lastMoveY) * dragRate
                lastMoveY = y
                when (dragMode) {
                    N_DOWN -> {
                        if (overScroll < 0) {
                            overScroll = 0f
                            viewRefreshContainer.translationY = overScroll
                            viewTarget!!.translationY = overScroll
                            return false
                        }
                        if (overScroll > viewContentHeight / 2) {
                            overScroll = viewContentHeight / 2.toFloat()
                        }
                        viewRefreshContainer.translationY = overScroll / 2
                        viewTarget!!.translationY = overScroll
                        if (!isRefreshing) {
                            viewRefresh.setHeight(
                                overScroll,
                                refreshMidHeight.toFloat(),
                                viewContentHeight.toFloat()
                            )
                        }
                        if (!isRefreshing) {
                            if (overScroll > refreshMidHeight) {
                                if (overScroll > secondFloorHeight && isCanSecondFloor) {
                                    viewRefresh.setReleaseToSecondFloor()
                                } else {
                                    viewRefresh.setReleaseToRefresh()
                                }
                            } else {
                                viewRefresh.setPullToRefresh()
                            }
                        }
                    }
                    N_UP -> {
                        if (overScroll > 0) {
                            overScroll = 0f
                            viewLoadContainer.translationY = overScroll
                            viewTarget!!.translationY = overScroll
                            return false
                        }
                        if (abs(overScroll) > viewContentHeight / 2)
                            overScroll = -viewContentHeight / 2.toFloat()
                        viewLoadContainer.translationY = overScroll / 2
                        viewTarget!!.translationY = overScroll
                        if (!isLoading) {
                            viewLoad.setHeight(
                                abs(overScroll),
                                loadMidHeight.toFloat(),
                                viewContentHeight.toFloat()
                            )
                        }
                        if (!isLoading) {
                            if (overScroll < -loadMidHeight) {
                                viewLoad.setReleaseToRefresh()
                            } else {
                                viewLoad.setPullToRefresh()
                            }
                        }
                    }
                    R_UP -> {
                        if (overScroll < 0) {
                            overScroll = 0f
                            viewRefreshContainer.translationY = overScroll
                            viewTarget!!.translationY = overScroll
                            return false
                        }
                        viewRefreshContainer.translationY = overScroll / 2
                        viewTarget!!.translationY = overScroll
                    }
                    L_DOWN -> {
                        if (overScroll > 0) {
                            overScroll = 0f
                            viewTarget!!.translationY = overScroll
                            viewLoadContainer.translationY = overScroll
                            return false
                        }
                        viewTarget!!.translationY = overScroll
                        viewLoadContainer.translationY = overScroll / 2
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                onTouchUp()
                dragMode = 0
                return false
            }
        }
        return true
    }

    override fun requestDisallowInterceptTouchEvent(b: Boolean) {
        if (Build.VERSION.SDK_INT < 21 && viewTarget is AbsListView
            || viewTarget != null
            && !ViewCompat.isNestedScrollingEnabled(viewTarget!!)
        ) {
        } else {
            super.requestDisallowInterceptTouchEvent(b)
        }
    }

    // 处理与父view之间的关联滑动
    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return isEnabled && !mReturningToStart && !isRefreshing && !isLoading && nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes)
        startNestedScroll(axes and ViewCompat.SCROLL_AXIS_VERTICAL)
        isTouchDown = true
        nestedOverScroll = 0f
        isNestedScrolling = true
    }

    override fun getNestedScrollAxes(): Int {
        return mNestedScrollingParentHelper.nestedScrollAxes
    }

    override fun onStopNestedScroll(target: View) {
        mNestedScrollingParentHelper.onStopNestedScroll(target)
        isNestedScrolling = false
        isTouchDown = false
        onTouchUp()
        nestedOverScroll = 0f
        stopNestedScroll()
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        if (dy > 0) isPullingUp = true
        if (nestedOverScroll > 0 && dy > 0 || nestedOverScroll < 0 && dy < 0) {
            nestedOverScroll -= dy.toFloat()
            consumed[1] = dy
            onNestedDraging(nestedOverScroll)
        }
        val parentConsumed = mParentScrollConsumed
        if (dispatchNestedPreScroll(
                dx - consumed[0],
                dy - consumed[1],
                parentConsumed,
                null
            )
        ) {
            consumed[0] += parentConsumed[0]
            consumed[1] += parentConsumed[1]
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            mParentOffsetInWindow
        )
        var dy = dyUnconsumed + mParentOffsetInWindow[1]
        if (dy > 0 && !canChildScrollUp()) {
            if (dy > 50) dy = 50
            nestedOverScroll -= dy.toFloat()
        }
        if (dy < 0 && !canChildScrollDown()) {
            if (dy < -50) dy = -50
            nestedOverScroll -= dy.toFloat()
        }
        onNestedDraging(nestedOverScroll)
    }

    private fun onNestedDraging(offset: Float) {
        overScroll = offset * dragRate * 0.7f
        if (overScroll > 0) {
            if (overScroll > viewContentHeight / 2) {
                overScroll = viewContentHeight / 2.toFloat()
            }
            viewRefreshContainer.translationY = overScroll / 2
            viewTarget!!.translationY = overScroll
            viewRefresh.setHeight(
                overScroll,
                refreshMidHeight.toFloat(),
                viewContentHeight.toFloat()
            )
            if (overScroll > refreshMidHeight) {
                if (overScroll > secondFloorHeight && isCanSecondFloor && !isRefreshing) {
                    viewRefresh.setReleaseToSecondFloor()
                } else {
                    viewRefresh.setReleaseToRefresh()
                }
            } else {
                viewRefresh.setPullToRefresh()
            }
        } else {
            if (!isLoadEnable) return
            if (overScroll < -viewContentHeight / 2) {
                overScroll = -viewContentHeight / 2.toFloat()
            }
            viewLoadContainer.translationY = overScroll / 2
            viewTarget!!.translationY = overScroll
            viewLoad.setHeight(
                abs(overScroll),
                loadMidHeight.toFloat(),
                viewContentHeight.toFloat()
            )
            if (overScroll < -loadMidHeight) {
                viewLoad.setReleaseToRefresh()
            } else {
                viewLoad.setPullToRefresh()
            }
        }
    }

    private fun onTouchUp() {
        if (overScroll == 0f) return
        if (overScroll > 0) {
            if (overScroll > refreshMidHeight) {
                if (overScroll > secondFloorHeight && isCanSecondFloor && !isRefreshing) {
                    animateToSecondFloor()
                } else {
                    animateToRefresh()
                }
            } else {
                if (!isRefreshing)
                    animateToRefreshReset()
            }
        } else {
            if (!isLoadEnable) return
            if (overScroll < -loadMidHeight) {
                animateToLoad()
            } else {
                if (!isLoading) {
                    animateToLoadReset()
                }
            }
        }
    }

    /*---------------------处理子view之间滑动关系 目前没用------------------*/
    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mNestedScrollingChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return mNestedScrollingChildHelper.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return mNestedScrollingChildHelper.hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
        dyUnconsumed: Int, offsetInWindow: IntArray?
    ): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedScroll(
            dxConsumed, dyConsumed,
            dxUnconsumed, dyUnconsumed, offsetInWindow
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?
    ): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun onNestedFling(
        target: View, velocityX: Float, velocityY: Float, consumed: Boolean
    ): Boolean {
        return dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedFling(
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    /**
     * 动画移动到刷新位置
     */
    private fun animateToRefresh() {
        if (isAnimating) return
        isAnimating = true
        animatorToRefresh?.setFloatValues(abs(overScroll), refreshHeight.toFloat())
        if (animatorToRefresh == null) {
            animatorToRefresh = ValueAnimator.ofFloat(abs(overScroll), refreshHeight.toFloat())
            animatorToRefresh!!.addUpdateListener { animation ->
                val height = animation.animatedValue as Float
                overScroll = height
                viewRefreshContainer.translationY = overScroll / 2
                if (!isRefreshing) {
                    viewRefresh.setHeight(
                        overScroll,
                        refreshMidHeight.toFloat(),
                        viewContentHeight.toFloat()
                    )
                }
                viewTarget!!.translationY = overScroll
                if (height == refreshHeight.toFloat()) {
                    if (!isRefreshing) {
                        viewRefresh.setRefresh()
                        isRefreshing = true
                        if (refreshListener != null) {
                            refreshListener?.invoke()
                        }
                    }
                    isAnimating = false
                }
            }
            animatorToRefresh!!.duration = animateDuration.toLong()
        }
        animatorToRefresh?.start()
    }

    /**
     * 动画移动到加载更多位置
     */
    private fun animateToLoad() {
        if (isAnimating) return
        isAnimating = true
        animatorToLoad?.setFloatValues(overScroll, -loadHeight.toFloat())
        if (animatorToLoad == null) {
            animatorToLoad = ValueAnimator.ofFloat(overScroll, -loadHeight.toFloat())
            animatorToLoad!!.addUpdateListener { animation ->
                val height = animation.animatedValue as Float
                overScroll = height
                viewLoadContainer.translationY = overScroll / 2
                if (!isLoading) {
                    viewLoad.setHeight(
                        abs(overScroll),
                        loadMidHeight.toFloat(),
                        viewContentHeight.toFloat()
                    )
                }
                viewTarget!!.translationY = overScroll
                if (height == -loadHeight.toFloat()) {
                    if (!isLoading) {
                        viewLoad.setRefresh()
                        isLoading = true
                        if (loadListener != null) {
                            loadListener?.invoke()
                        }
                    }
                    isAnimating = false
                }
            }
            animatorToLoad!!.duration = animateDuration.toLong()
        }
        animatorToLoad!!.start()
        isPullingUp = false
    }

    /**
     * 动画移动到刷新初始位置
     */
    private fun animateToRefreshReset() {
        if (overScroll == 0f) {
            isRefreshing = false
        } else {
            if (isAnimating) return
            isAnimating = true
            animatorToRefreshReset?.setFloatValues(abs(overScroll), 0f)
            if (animatorToRefreshReset == null) {
                animatorToRefreshReset = ValueAnimator.ofFloat(abs(overScroll), 0f)
                animatorToRefreshReset!!.addUpdateListener { animation ->
                    val height = animation.animatedValue as Float
                    overScroll = height
                    viewRefreshContainer.translationY = overScroll / 2
                    viewRefresh.setHeight(
                        overScroll,
                        refreshMidHeight.toFloat(),
                        viewContentHeight.toFloat()
                    )
                    viewTarget!!.translationY = overScroll
                    isRefreshing = false
                    if (height == 0f) {
                        isAnimating = false
                    }
                }
                animatorToRefreshReset!!.duration = animateDuration.toLong()
            }
            animatorToRefreshReset!!.start()
        }
    }

    /**
     * 动画移动到加载更多初始位置
     */
    private fun animateToLoadReset() {
        if (overScroll == 0f) {
            isLoading = false
        } else {
            if (isAnimating) return
            isAnimating = true
            animatorToLoadReset?.setFloatValues(overScroll, 0f)
            if (animatorToLoadReset == null) {
                animatorToLoadReset = ValueAnimator.ofFloat(overScroll, 0f)
                animatorToLoadReset!!.addUpdateListener { animation ->
                    val height = animation.animatedValue as Float
                    overScroll = height
                    viewLoadContainer.translationY = overScroll / 2
                    viewLoad.setHeight(
                        abs(overScroll),
                        loadMidHeight.toFloat(),
                        viewContentHeight.toFloat()
                    )
                    viewTarget!!.translationY = overScroll
                    isLoading = false
                    if (height == 0f) {
                        isAnimating = false
                    }
                }
                animatorToLoadReset!!.duration = animateDuration.toLong()
            }
            animatorToLoadReset!!.start()
        }
    }

    private fun animateToSecondFloor() {
        if (isAnimating) return
        isAnimating = true
        animatorToSecondFloor?.setFloatValues(overScroll, viewContentHeight.toFloat())
        if (animatorToSecondFloor == null) {
            animatorToSecondFloor = ValueAnimator.ofFloat(overScroll, viewContentHeight.toFloat())
            animatorToSecondFloor!!.addUpdateListener { animation ->
                val height = animation.animatedValue as Float
                overScroll = height
                viewRefreshContainer.translationY = overScroll / 2
                viewLoadContainer.translationY = overScroll / 2
                viewRefresh.setHeight(
                    abs(overScroll),
                    loadMidHeight.toFloat(),
                    viewContentHeight.toFloat()
                )
                viewTarget!!.translationY = overScroll
                if (height == viewContentHeight.toFloat()) {
                    isAnimating = false
                    isSecondFloor = true
                    viewRefresh.setToSecondFloor()
                }
            }
            animatorToSecondFloor!!.duration = animateDuration.toLong()
        }
        animatorToSecondFloor!!.start()
    }

    private fun animateToFirstFloor() {
        if (isAnimating) return
        isAnimating = true
        animatorToFirstFloor?.setFloatValues(overScroll, 0f)
        if (animatorToFirstFloor == null) {
            animatorToFirstFloor = ValueAnimator.ofFloat(overScroll, 0f)
            animatorToFirstFloor!!.addUpdateListener { animation ->
                val height = animation.animatedValue as Float
                overScroll = height
                viewRefreshContainer.translationY = overScroll / 2
                viewLoadContainer.translationY = overScroll / 2
                viewRefresh.setHeight(
                    abs(overScroll),
                    loadMidHeight.toFloat(),
                    viewContentHeight.toFloat()
                )
                viewTarget!!.translationY = overScroll
                if (height == 0f) {
                    isAnimating = false
                    isSecondFloor = false
                    viewRefresh.setToFirstFloor()
                }
            }
        }
        animatorToFirstFloor!!.start()
    }

    /**
     * 全部恢复到初始位置
     */
    private fun reset() {
        if (animatorToRefresh != null) animatorToRefresh!!.cancel()
        if (animatorToRefreshReset != null) animatorToRefreshReset!!.cancel()
        if (animatorToLoad != null) animatorToLoad!!.cancel()
        if (animatorToLoadReset != null) animatorToLoadReset!!.cancel()
        if (animatorToSecondFloor != null) animatorToSecondFloor!!.cancel()
        viewRefreshContainer.translationY = 0f
        viewLoadContainer.translationY = 0f
        viewTarget!!.translationY = 0f
    }

    /**
     * 目标是否可以向下滚动
     */
    private fun canChildScrollDown(): Boolean {
        return if (viewTarget is ListView) {
            ListViewCompat.canScrollList((viewTarget as ListView?)!!, -1)
        } else viewTarget!!.canScrollVertically(-1)
    }

    /**
     * 目标是否可以向上滚动
     */
    private fun canChildScrollUp(): Boolean {
        return if (viewTarget is ListView) {
            ListViewCompat.canScrollList((viewTarget as ListView?)!!, 1)
        } else viewTarget!!.canScrollVertically(1)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        reset()
    }

    interface ListScrollListener {
        fun onScrollStateChanged(view: AbsListView?, scrollState: Int)
        fun onScroll(
            view: AbsListView?,
            firstVisibleItem: Int,
            visibleItemCount: Int,
            totalItemCount: Int
        )
    }

    fun setOnChildScrollUpCallback(callback: (parent: RefreshLayout, child: View?) -> Boolean) {
        mChildScrollUpCallback = callback
    }

    interface OnChildScrollUpCallback {
        fun canChildScrollUp(parent: RefreshLayout, child: View?): Boolean
    }
}