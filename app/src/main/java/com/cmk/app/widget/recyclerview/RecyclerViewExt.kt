package com.cmk.app.widget.recyclerview

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmk.app.ext.dp2px

/**
 * Runs [action] on every visible [RecyclerView.ViewHolder] in this [RecyclerView].
 */
inline fun <reified T : RecyclerView.ViewHolder> RecyclerView.forEachVisibleHolder(
    action: (T) -> Unit
) {
    for (i in 0 until childCount) {
        action(getChildViewHolder(getChildAt(i)) as T)
    }
}

/** The magnitude of translation distance while the list is over-scrolled. */
const val OVERSCROLL_TRANSLATION_MAGNITUDE = 0.2f

/** The magnitude of translation distance when the list reaches the edge on fling. */
const val FLING_TRANSLATION_MAGNITUDE = 0.5f

/**
 * Add item padding
 * @param padding the top, bottom, left, right is same
 */
fun RecyclerView.itemPadding(padding: Int) {
    addItemDecoration(PaddingItemDecoration(padding, padding, padding, padding))
}

/**
 * Add item padding for top, bottom, left, right
 */
fun RecyclerView.itemPadding(top: Int, bottom: Int, left: Int = 0, right: Int = 0) {
    addItemDecoration(PaddingItemDecoration(top, bottom, left, right))
}

/**
 * Add item padding for top, bottom, left, right
 */
fun RecyclerView.itemPadding(left: Int, right: Int) {
    addItemDecoration(PaddingItemDecoration(top, bottom, left, right))
}

fun RecyclerView.itemPadding2(space: Int, edgeSpace: Int) {
    addItemDecoration(GridPaddingItemDecoration(space, edgeSpace))
}

fun RecyclerView.itemPadding1(space: Int) {
    addItemDecoration(GridItemDecoration(space))
}

fun RecyclerView.itemPadding3(spanCount: Int, edgeSpace: Int, includeEdge:Boolean){
    addItemDecoration(ItemDecorationInset(spanCount, edgeSpace, includeEdge))
}

class PaddingItemDecoration(top: Int, bottom: Int, left: Int, right: Int) :
    RecyclerView.ItemDecoration() {

    private val mTop = top
    private val mLeft = left
    private val mRight = right
    private val mBottom = bottom

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = view.dp2px(mLeft)
        outRect.top = view.dp2px(mTop)
        outRect.right = view.dp2px(mRight)
        outRect.bottom = view.dp2px(mBottom)
    }
}

/**
 * @param mSpace     item间的间距
 * @param mEdgeSpace 边距(padding)
 */
class GridPaddingItemDecoration(space: Int) : RecyclerView.ItemDecoration() {

    private var mSpace: Int = 0
    private var mEdgeSpace: Int = 0

    constructor(space: Int, edgeSpace: Int) : this(space) {
        this.mSpace = space
        this.mEdgeSpace = edgeSpace
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val manager = parent.layoutManager
        val childPosition = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount
//        if (manager is GridLayoutManager) {
//            setGridOffset(
//                manager.orientation,
//                manager.spanCount,
//                outRect,
//                childPosition,
//                itemCount!!
//            )
//        }
        when (manager) {
            is GridLayoutManager -> {
                setGridOffset(
                    manager.orientation,
                    manager.spanCount,
                    outRect,
                    childPosition,
                    itemCount!!
                )
            }
            is LinearLayoutManager -> {
                setLinearOffset(
                    manager.orientation,
                    outRect,
                    childPosition,
                    itemCount!!
                )
            }

        }
    }

    private fun setLinearOffset(
        orientation: Int,
        outRect: Rect,
        childPosition: Int,
        itemCount: Int
    ) {
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            when (childPosition) {
                0 -> {
                    // 第一个要设置PaddingLeft
                    outRect[mEdgeSpace, 0, mSpace] = 0
                }
                itemCount - 1 -> {
                    // 最后一个设置PaddingRight
                    outRect[0, 0, mEdgeSpace] = 0
                }
                else -> {
                    outRect[0, 0, mSpace] = 0
                }
            }
        } else {
            when (childPosition) {
                0 -> {
                    // 第一个要设置PaddingTop
                    outRect[0, mEdgeSpace, 0] = mSpace
                }
                itemCount - 1 -> {
                    // 最后一个要设置PaddingBottom
                    outRect[0, 0, 0] = mEdgeSpace
                }
                else -> {
                    outRect[0, 0, 0] = mSpace
                }
            }
        }
    }

    /**
     * 设置GridLayoutManager 类型的 offest
     *
     * @param orientation   方向
     * @param spanCount     个数
     * @param outRect       padding
     * @param childPosition 在 list 中的 postion
     * @param itemCount     list size
     */
    private fun setGridOffset(
        orientation: Int,
        spanCount: Int,
        outRect: Rect,
        childPosition: Int,
        itemCount: Int
    ) {
        val totalSpace = mSpace * (spanCount - 1) + mEdgeSpace * 2 // 总共的padding值
        val eachSpace = totalSpace / spanCount // 分配给每个item的padding值
        val column = childPosition % spanCount // 列数
        val row = childPosition / spanCount // 行数
        var left: Int
        var right: Int
        var top: Int
        var bottom: Int
        if (orientation == GridLayoutManager.VERTICAL) {
            top = 0 // 默认 top为0
            bottom = mSpace // 默认bottom为间距值
            if (mEdgeSpace == 0) {
                left = column * eachSpace / (spanCount - 1)
                right = eachSpace - left
                // 无边距的话  只有最后一行bottom为0
                if (itemCount / spanCount == row) {
                    bottom = 0
                }
            } else {
                if (childPosition < spanCount) {
                    // 有边距的话 第一行top为边距值
                    top = mEdgeSpace
                } else if (itemCount / spanCount == row) {
                    // 有边距的话 最后一行bottom为边距值
                    bottom = mEdgeSpace
                }
                left = column * (eachSpace - mEdgeSpace - mEdgeSpace) / (spanCount - 1) + mEdgeSpace
                right = eachSpace - left
            }
        } else {
            // orientation == GridLayoutManager.HORIZONTAL 跟上面的大同小异, 将top,bottom替换为left,right即可
            left = 0
            right = mSpace
            if (mEdgeSpace == 0) {
                top = column * eachSpace / (spanCount - 1)
                bottom = eachSpace - top
                if (itemCount / spanCount == row) {
                    right = 0
                }
            } else {
                if (childPosition < spanCount) {
                    left = mEdgeSpace
                } else if (itemCount / spanCount == row) {
                    right = mEdgeSpace
                }
                top = column * (eachSpace - mEdgeSpace - mEdgeSpace) / (spanCount - 1) + mEdgeSpace
                bottom = eachSpace - top
            }
        }
        outRect[left, top, right] = bottom
    }
}

class GridItemDecoration @JvmOverloads constructor(
    private val horizontalSpace: Int,
    private val verticalSpace: Int,
    private val color: Int = Color.TRANSPARENT
) : RecyclerView.ItemDecoration() {

    private val space = 0
    private var mPaint: Paint? = null
    /**
     * 自定义宽度的透明分割线
     *
     * @param space 指定宽度
     */
    /**
     * 默认的，垂直方向 横纵1px 的分割线 颜色透明
     */
    @JvmOverloads
    constructor(space: Int = 1) : this(space, space, Color.TRANSPARENT) {
    }

    /**
     * 自定义宽度，并指定颜色的分割线
     *
     * @param space 指定宽度
     * @param color 指定颜色
     */
    init {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint?.color = color
        mPaint?.style = Paint.Style.FILL
        mPaint?.strokeWidth = space.toFloat()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val layoutManager = parent.layoutManager
        val hOffset = horizontalSpace / 2
        val vOffset = verticalSpace / 2
        if (layoutManager is GridLayoutManager) {
            val span = layoutManager.spanCount
            // view 位置
            val childPosition = parent.getChildAdapterPosition(view)
            // 第一排，顶部为verticalSpace
            if (childPosition < span) {

                if (childPosition % span == 0) { // 最左边，左边是horizonTalSpace，右边是hOffset
                    outRect[horizontalSpace, verticalSpace, hOffset] = vOffset
                } else if (childPosition % span == span - 1) { // 最右边，左边是hOffset，右边是HroizonTalSpace
                    outRect[hOffset, verticalSpace, horizontalSpace] = vOffset
                }
                if (childPosition % span == 0) {
                    outRect[horizontalSpace, vOffset, hOffset] = vOffset
                } else if (childPosition % span == span - 1) {
                    outRect[hOffset, vOffset, horizontalSpace] = vOffset
                }
            }
        }
        //        else if (layoutManager instanceof LinearLayoutManager){
//            int childPosition = parent.getChildAdapterPosition(view);
//            if (childPosition <space) {
//                outRect.set(horizontalSpace, verticalSpace, horizontalSpace, vOffset);
//            } else {
//                outRect.set(horizontalSpace, vOffset, hOffset, vOffset);
//            }
//        }
    }


    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
    }
}

class ItemDecorationInset(
    private val mSpanCount: Int,
    private val mSpacing: Int,
    private val mIncludeEdge: Boolean
) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % mSpanCount // item column
        if (mIncludeEdge) {
            // spacing - column * ((1f / spanCount) * spacing)
            outRect.left = mSpacing - column * mSpacing / mSpanCount
            // (column + 1) * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * mSpacing / mSpanCount
            if (position < mSpanCount) { // top edge
                outRect.top = mSpacing
            }
            outRect.bottom = mSpacing // item bottom
        } else {
            // column * ((1f / spanCount) * spacing)
            outRect.left = column * mSpacing / mSpanCount
            // spacing - (column + 1) * ((1f / spanCount) * spacing)
            outRect.right = mSpacing - (column + 1) * mSpacing / mSpanCount
            if (position >= mSpanCount) {
                outRect.top = mSpacing // item top
            }
        }
    }
}



