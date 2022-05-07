package com.cmk.app.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.cmk.app.R
import com.cmk.app.ext.dp2px
import com.cmk.app.test.T
import java.io.File

/**
 *  填写短信验证码
 */
class InputNumEditText(context: Context?) : AppCompatEditText(context!!) {

    private var mContext: Context? = null
    private var lineColor: Int = Color.BLACK
    private var codeColor: Int = Color.BLACK
    private var lineHeight: Float = dp2px(1).toFloat()
    private var codeLength: Int = 4// 默认验证码长度4位
    private var mWidth = -1
    private var mHeight = -1
    private var lineWidth = -1F
    private var spaceLineWidth = -1F
    private var codeBaseLine = -1F
    private lateinit var linePaint: Paint
    private lateinit var textPaint: Paint
    private lateinit var rectF: RectF

    constructor(context: Context?, attrs: AttributeSet?) : this(context) {
        val ta = context?.obtainStyledAttributes(attrs, R.styleable.InputNumEditText)
        try {
            ta?.let {
                lineColor = it.getColor(R.styleable.InputNumEditText_lineColor, Color.BLACK)
                lineHeight =
                    it.getDimension(R.styleable.InputNumEditText_strokeWidth, dp2px(1).toFloat())
                codeColor = it.getColor(R.styleable.InputNumEditText_codeColor, Color.BLACK)
                codeLength = it.getInteger(R.styleable.InputNumEditText_codeLength, 4)
            }
        } finally {
            ta?.recycle()
        }

        linePaint = Paint().apply {
            isAntiAlias = true
            color = lineColor
            style = Paint.Style.FILL
            strokeWidth = lineHeight
        }

        textPaint = Paint().apply {
            isAntiAlias = true
            color = codeColor
//            strokeWidth = 8F
            if (codeLength == 4)
                textSize = dp2px(32).toFloat()
            else if (codeLength == 6)
                textSize = dp2px(24).toFloat()
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
        }

        rectF = RectF(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            paddingLeft.toFloat() + lineWidth,
            mHeight.toFloat()
        )
        val imageToBase64 = T.imageToBase64(File(""))
        this.filters = arrayOf<InputFilter>(LengthFilter(codeLength))
    }

    init {
        this.mContext = context
        this.setTextColor(Color.TRANSPARENT)//妈的，暂时想不出来啥办法覆盖setText的方法，只能这么整
        this.background = null
        this.inputType = InputType.TYPE_CLASS_PHONE
        this.isCursorVisible = false
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (i in 0 until codeLength) {
            canvas?.drawLine(
                paddingLeft + (lineWidth + spaceLineWidth) * i,
                mHeight.toFloat(),
                paddingLeft + lineWidth * (i + 1) + spaceLineWidth * i,
                mHeight.toFloat(),
                linePaint
            )
        }

        val textInput = this.text.toString()
        for (i in textInput.indices) {
            rectF.left = paddingLeft + (lineWidth + spaceLineWidth) * i
            rectF.right = paddingLeft + lineWidth * (i + 1) + spaceLineWidth * i
            canvas?.drawText(textInput[i].toString(), rectF.centerX(), codeBaseLine, textPaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth
        mHeight = measuredHeight
        // 底部下划线测量
        if (codeLength == 4) {
            lineWidth = mWidth / 6F
        } else if (codeLength == 6) {
            lineWidth = mWidth / 8F
        }
        spaceLineWidth = (mWidth - lineWidth * codeLength) / (codeLength - 1)
        // 验证码测量
        val fontMetrics = textPaint.fontMetrics
        val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        rectF.bottom = mHeight.toFloat()
         codeBaseLine = rectF.centerY() + distance
    }
}