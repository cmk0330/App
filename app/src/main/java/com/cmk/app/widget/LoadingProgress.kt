package com.cmk.app.widget

import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import com.cmk.app.R
import com.cmk.app.ext.dp2px

class LoadingProgress : View {

    private lateinit var annulusPaint: Paint
    private lateinit var abgPaint: Paint
    private lateinit var circlePaint: Paint
    private lateinit var textPaint: Paint
    private var annulusWidth = 10F
    private var annulusColor = 0x000000
    private var loadingProgressSize = 90F
    private var annulusBackgroundColor = 0x00808080
    private var circleColor = 0xffffff
    private var percentColor = 0x000000
    private var percentSize = dp2px(24).toFloat()
    private var textBaseLine = 0F
    private var textDistanceX = 0F
    private var rectR = 0
    private var circleR = 0F
    private lateinit var annulusRect: RectF
    private lateinit var bgRect: RectF
    private lateinit var textRect: RectF
    private var mWidth = 0
    private var mHeight = 0
    private var mProgress = 0F
    private var max = 100
    private var text = ""

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attr: AttributeSet, defStyleAttr: Int) : super(
        context,
        attr,
        defStyleAttr
    )

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        val ta = context?.obtainStyledAttributes(attrs, R.styleable.LoadingProgress)
        try {
            ta?.let {
                annulusWidth = it.getDimension(R.styleable.LoadingProgress_annulusWidth, 1F)
                annulusColor = it.getColor(R.styleable.LoadingProgress_annulusColor, 0x000000)
                loadingProgressSize =
                    it.getDimension(R.styleable.LoadingProgress_loadingProgressSize, 90F)
                annulusBackgroundColor =
                    it.getColor(R.styleable.LoadingProgress_annulusBackgroundColor, 0x00808080)
                circleColor = it.getColor(R.styleable.LoadingProgress_innerColor, 0xffffff)
                percentColor = it.getColor(R.styleable.LoadingProgress_percentColor, 0x000000)
                percentSize =
                    it.getDimension(R.styleable.LoadingProgress_percentSize, dp2px(24).toFloat())
            }
        } finally {
            ta?.recycle()
        }

        annulusPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            color = annulusColor
            strokeWidth = annulusWidth
        }

        abgPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeWidth = annulusWidth
            color = annulusBackgroundColor
        }

        circlePaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.FILL
            color = circleColor
        }

        textPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.FILL
            color = percentColor
            textSize = percentSize
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawArc(bgRect, 0F, 360F, false, abgPaint)
        canvas?.drawArc(annulusRect, -90F, 360F * mProgress / max, false, annulusPaint)
        canvas?.drawCircle(rectR / 2F, rectR / 2F, circleR, circlePaint)
        canvas?.drawText("$text${mProgress.toInt()}", textDistanceX, textBaseLine, textPaint)
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth
        mHeight = measuredHeight
        rectR = mWidth.coerceAtMost(mHeight)
        //圆环progress
        annulusRect = RectF(
            paddingLeft.toFloat() + annulusWidth / 2,
            paddingTop.toFloat() + annulusWidth / 2,
            rectR - paddingRight.toFloat() - annulusWidth / 2,
            rectR - paddingBottom.toFloat() - annulusWidth / 2
        )
        // 圆环背景
        bgRect = RectF(
            paddingLeft.toFloat() + annulusWidth / 2,
            paddingTop.toFloat() + annulusWidth / 2,
            rectR - paddingRight.toFloat() - annulusWidth / 2,
            rectR - paddingBottom.toFloat() - annulusWidth / 2
        )
        //内圆
        circleR = (rectR - paddingLeft.toFloat() - annulusPaint.strokeWidth - paddingRight.toFloat()
                - annulusPaint.strokeWidth) / 2
        //中间的百分比
        textRect = RectF(
            paddingLeft.toFloat() + abgPaint.strokeWidth * 2,
            paddingTop.toFloat() + abgPaint.strokeWidth * 2,
            rectR - paddingRight.toFloat() - abgPaint.strokeWidth * 2,
            rectR - paddingBottom.toFloat() - abgPaint.strokeWidth * 2
        )
        val fontMetrics = textPaint.fontMetrics
        val distanceY = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        val textWidth = textPaint.measureText("$text${mProgress.toInt()}")
        textBaseLine = textRect.centerY() + distanceY
        textDistanceX = textRect.centerX() - textWidth / 2
    }

    fun setProgress(progress: Float) {
        this.mProgress = progress
        invalidate()
    }

    fun setProgress(max: Int, progress: Float, animTime: Long? = 400) {
        if (progress <= 0) setProgress(progress)
        else {
            this.max = max
            val animator = ValueAnimator.ofFloat(mProgress, progress)
            animator.addUpdateListener {
                mProgress = it.animatedValue as Float
                invalidate()
            }
//        animator.interpolator = OvershootInterpolator()
            if (animTime != null)
                animator.duration = animTime
            else
                animator.duration = 100
            animator.start()
        }
    }

    fun setText(text: String) {
        this.text = text
        val textWidth = textPaint.measureText("$text${mProgress.toInt()}")
        textDistanceX = textRect.centerX() - textWidth / 2
    }

    class ProgressEvaluator : TypeEvaluator<Float> {
        override fun evaluate(fraction: Float, startValue: Float?, endValue: Float?): Float {
            return fraction
        }
    }
}