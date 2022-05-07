package com.cmk.app.widget.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.cmk.app.R
import kotlin.math.abs
import kotlin.math.pow

/**
 * Created by cuimingkun on 2019/11/21.
 */
class DefaultRefreshView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null
) : RefreshView(context, attrs) {
    private val imageView: CircleImageView
    private val tvSecondFloor: TextView
    private val viewContainer: View
    private val viewCover: View
    private val mProgress: CircularProgressDrawable

    fun setSecondFloorView(secondFloorView: View?) {
        secondFloorView?.layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        addView(secondFloorView, 0)
    }

    override fun setHeight(
        dragDistance: Float,
        distanceToRefresh: Float,
        totalDistance: Float
    ) {
        mProgress.stop()
        moveSpinner(dragDistance, distanceToRefresh, totalDistance)
        viewCover.alpha = (totalDistance - dragDistance) / totalDistance
    }

    override fun setRefresh() {
        mProgress.alpha = 255
        mProgress.arrowEnabled = false
        mProgress.progressRotation = 1f
        mProgress.start()
    }

    override fun setPullToRefresh() {
        tvSecondFloor.visibility = View.GONE
    }

    override fun setReleaseToRefresh() {
        tvSecondFloor.visibility = View.GONE
    }

    override fun setReleaseToSecondFloor() {
        tvSecondFloor.visibility = View.VISIBLE
    }

    override fun setToSecondFloor() {
        viewCover.alpha = 0f
        viewContainer.visibility = View.GONE
    }

    override fun setToFirstFloor() {
        viewCover.alpha = 1f
        viewContainer.visibility = View.VISIBLE
    }

    fun setColorSchemeColors(@ColorInt vararg colors: Int) {
        mProgress.setColorSchemeColors(*colors)
    }

    override fun setBackgroundColor(@ColorInt color: Int) {
        imageView.setBackgroundColor(color)
    }

    private fun moveSpinner(
        overScrollTop: Float,
        distanceToRefresh: Float,
        totalDragDistance: Float
    ) {
        var totalDragDistance = totalDragDistance
        mProgress.arrowEnabled = true
        totalDragDistance /= 10
        val density = context.resources.displayMetrics.density
        val originalDragPercent = overScrollTop / totalDragDistance
        val dragPercent = 1f.coerceAtMost(abs(originalDragPercent))
        val adjustedPercent = (dragPercent - 0.4).coerceAtLeast(0.0).toFloat() * 5 / 3
        val extraOS = abs(overScrollTop) - totalDragDistance
        val slingshotDist = 64 * density
        val tensionSlingshotPercent =
            0f.coerceAtLeast(extraOS.coerceAtMost(slingshotDist * 2) / slingshotDist)
        val tensionPercent = (tensionSlingshotPercent / 4 - (tensionSlingshotPercent / 4).toDouble()
            .pow(2.0)).toFloat() * 2f
        val strokeStart = adjustedPercent * 0.8f
        mProgress.setStartEndTrim(0f, 0.8f.coerceAtMost(strokeStart))
        mProgress.arrowScale = 1f.coerceAtMost(adjustedPercent)
        val rotation = (-0.25f + 0.4f * adjustedPercent + tensionPercent * 2) * 0.5f
        mProgress.progressRotation = rotation
        var alpha = (overScrollTop / distanceToRefresh * 255).toInt()
        if (alpha in 101..254) alpha = 100
        if (alpha > 255) alpha = 255
        mProgress.alpha = alpha
    }

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_default_refresh, null)
        addView(view)
        viewContainer = view.findViewById(R.id.viewContainer)
        tvSecondFloor = view.findViewById(R.id.tvSecondFloor)
        viewCover = view.findViewById(R.id.viewCover)
        imageView = view.findViewById(R.id.imageView)
        mProgress = CircularProgressDrawable(getContext())
        mProgress.setStyle(CircularProgressDrawable.DEFAULT)
        imageView.setImageDrawable(mProgress)
        mProgress.arrowEnabled = true
        mProgress.stop()
    }
}