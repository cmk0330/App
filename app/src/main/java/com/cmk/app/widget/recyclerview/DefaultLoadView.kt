package com.cmk.app.widget.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView

/**
 * Created by cuimingkun on 2019/11/21.
 */
class DefaultLoadView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    LoadView(context, attrs) {
    private val tvContent: TextView
    private val progressBar: ProgressBar

    override fun setHeight(
        dragDistance: Float,
        distanceToRefresh: Float,
        totalDistance: Float
    ) {
    }

    override fun setRefresh() {
        tvContent.text = "正在加载更多"
        progressBar.visibility = View.VISIBLE
    }

    override fun setPullToRefresh() {
        progressBar.visibility = View.GONE
        tvContent.text = "上拉加载更多"
    }

    override fun setReleaseToRefresh() {
        progressBar.visibility = View.GONE
        tvContent.text = "释放加载更多"
    }

    init {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        tvContent = TextView(context)
        tvContent.id = View.generateViewId()
        addView(tvContent)
        val contentParams = tvContent.layoutParams as LayoutParams
        contentParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        progressBar = ProgressBar(context)
        addView(progressBar)
        val density = getContext().resources.displayMetrics.density
        val params = progressBar.layoutParams as LayoutParams
        params.width = (20 * density).toInt()
        params.height = (20 * density).toInt()
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        params.rightMargin = (10 * density).toInt()
        params.addRule(RelativeLayout.LEFT_OF, tvContent.id)
        progressBar.layoutParams = params
    }
}