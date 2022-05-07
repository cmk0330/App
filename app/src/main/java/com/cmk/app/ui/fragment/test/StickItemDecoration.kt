package com.cmk.app.ui.fragment.test

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * @Author: romens
 * @Description: java类作用描述
 * @CreateDate: 2020-11-9 16:02
 * @UpdateUser:
 * @UpdateDate: 2020-11-9 16:02
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
class StickItemDecoration(var groupInterface: GroupInterface?) : RecyclerView.ItemDecoration() {

    private var paint: Paint? = null

    init {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint?.color = Color.RED
        paint?.style = Paint.Style.FILL
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val index = parent.getChildAdapterPosition(view)
        groupInterface?.let {
            if(groupInterface!!.isFirst(index)){
                outRect.set(0, 100, 0, 0)
                return
            }
        }

        outRect.set(0, 5, 0, 0)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        val childCount = parent.childCount
        for (i in 0 until childCount){
            val view = parent.getChildAt(i)
            val index = parent.getChildAdapterPosition(view)

            if(groupInterface != null && groupInterface!!.isFirst(index)){
                paint?.let {
                    it.color = Color.BLUE
                    c.drawRect(view.left.toFloat(), (view.top - 100).toFloat(), view.right.toFloat(), view.top.toFloat(), it)
                    it.color = Color.GREEN
                    it.textSize = 100f
                    c.drawText(groupInterface!!.getGroupId(index), view.left.toFloat(), view.top.toFloat(), it)
                }
            } else {
                paint?.let {
                    it.color = Color.TRANSPARENT
                    c.drawRect(view.left.toFloat(), (view.top - 5).toFloat(), view.right.toFloat(), view.top.toFloat(), it)
                }
            }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val firstVisibleView = parent.getChildAt(0)

        val firstVisbleIndex = parent.getChildAdapterPosition(firstVisibleView)
        val firstVisbleGroupId = groupInterface?.getGroupId(firstVisbleIndex)

        if(firstVisibleView != null && firstVisibleView.top < 100){
            paint?.let {
                it.color = Color.BLUE
                c.drawRect(parent.left.toFloat(), 0f, parent.right.toFloat(), 100f, it)
                it.color = Color.GREEN
                c.drawText(firstVisbleGroupId!!, parent.left.toFloat(), 100f, it)
            }
        }
    }
}

interface GroupInterface{

    fun isFirst(index: Int): Boolean

    fun getGroupId(index: Int): String
}

data class GroupItem(var content: String, var isFirst: Boolean, var groupId: String)