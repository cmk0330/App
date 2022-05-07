package com.cmk.app.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.cmk.app.ext.dp2px

class CustomCircle(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    init {

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val paint = Paint()
        paint.color = Color.parseColor("#F45A8D")
        paint.isAntiAlias  = true
        paint.strokeWidth = dp2px(10).toFloat()
        canvas?.drawColor(Color.parseColor("#00BBFF"))

        canvas?.drawCircle(240F, 240F, 200F,paint)
        paint.style = Paint.Style.STROKE
        paint.color = Color.parseColor("#00AA00")
        canvas?.drawCircle(720F,240F, 200F,paint)

        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#F45A8D")
        val viewGroup = parent as ViewGroup
       canvas?.drawRect(700F, 100F, 1100F, 500F,paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(MeasureSpec.EXACTLY, MeasureSpec.EXACTLY)
        var width = getSize(widthMeasureSpec, widthMeasureSpec)
        val height = getSize(100, heightMeasureSpec)


        setMeasuredDimension(width, height)
    }

    private fun getSize(defaultSize: Int, measureSpec: Int): Int {
        var size = 0
        val measureMode = MeasureSpec.getMode(measureSpec)
        val measureSize = MeasureSpec.getSize(measureSpec)
        when (measureMode) {
            MeasureSpec.UNSPECIFIED -> {
                //如果没有指定大小，就设置为默认大小   客源分配   （您暂未获得授权）
                size = defaultSize
            }
            MeasureSpec.AT_MOST -> {
                //如果测量模式是最大取值为size
//我们将大小取最大值,你也可以取其他值
                size = measureSize
            }
            MeasureSpec.EXACTLY -> {
                //如果是固定的大小，那就不要去改变它
                size = measureSize
            }
        }
        return size
    }
}