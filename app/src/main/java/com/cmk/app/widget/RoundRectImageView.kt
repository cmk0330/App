package com.cmk.app.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.cmk.app.R
import com.cmk.app.ext.dp2px

class RoundRectImageView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context!!, attrs, defStyle) {
    private var mWidth = 0
    private var mHeight = 0
    private var borderWidth: Int = 0
    private var borderColor: Int = 0x333333
    private var roundRadius: Int = dp2px(4)
    private var mPaint = Paint()
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val sharedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var srcBitmap: Bitmap? = null
    private var desBitmap: Bitmap? = null
    private val bitmapRect = RectF(0f, 0f, 0f, 0f)
    private val rectSrc = Rect(0, 0, 0, 0)
    private val rectDst = Rect(0, 0, 0, 0)
    private val bitmapMatrix = Matrix()

    init {
        val ta = context?.obtainStyledAttributes(attrs, R.styleable.RoundRectImageView, defStyle, 0)
        try {
            ta?.apply {
                borderWidth =
                    getDimensionPixelSize(R.styleable.RoundRectImageView_borderWidth, dp2px(2))
                borderColor = getColor(R.styleable.RoundRectImageView_borderColor, 0x333333)
                roundRadius =
                    getDimensionPixelOffset(R.styleable.RoundRectImageView_roundRadius, dp2px(4))
            }
        } finally {
            ta?.recycle()
        }
        borderPaint.apply {
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = borderWidth.toFloat()
        }

    }

    /**
     * 绘制圆角矩形图片
     * @author caizhiming
     */
    override fun onDraw(canvas: Canvas) {
        if (drawable == null) {
            super.onDraw(canvas)
            return
        }
        desBitmap?.let { canvas.drawBitmap(it, rectSrc, rectDst, mPaint) }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = width
        val height = height
        val measuredHeight = measuredHeight
        val measuredWidth = measuredWidth
        mWidth = width
        mHeight = height

        srcBitmap = drawableToBitmap(drawable)
        desBitmap = getRoundBitmapByShader(srcBitmap, mWidth, mHeight)
        rectDst.right = mWidth
        rectDst.bottom = mHeight


    }

    /**
     * 把资源图片转换成Bitmap
     * @param drawable
     * 资源图片
     * @return 位图
     */
    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        // 当设置不为图片，为颜色时，获取的drawable宽高会有问题，所有当为颜色时候获取控件的宽高
        val width = if (drawable.intrinsicWidth <= 0) mWidth else drawable.intrinsicWidth
        val height = if (drawable.intrinsicHeight <= 0) mHeight else drawable.intrinsicHeight
        val bitmap = Bitmap.createBitmap(
            width,
            height,
            if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        )
        val canvas = Canvas(bitmap)
        //drawable.setBounds(-4, -4, width + 4, height + 4);
        drawable.draw(canvas)
        return bitmap
    }


    private fun getRoundBitmapByShader(
        bitmap: Bitmap?,
        outWidth: Int,
        outHeight: Int
    ): Bitmap? {
        if (bitmap == null) {
            return null
        }
        val width = bitmap.width
        val height = bitmap.height
        val widthScale = outWidth * 1f / width
        val heightScale = outHeight * 1f / height

        bitmapMatrix.setScale(widthScale, heightScale)
        //创建输出的bitmap
        val desBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888)
        //创建canvas并传入desBitmap，这样绘制的内容都会在desBitmap上
        val canvas = Canvas(desBitmap)
        rectSrc.right = desBitmap.width
        rectSrc.bottom = desBitmap.height

        //创建着色器
        val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        //给着色器配置matrix
        bitmapShader.setLocalMatrix(bitmapMatrix)
        sharedPaint.shader = bitmapShader
        //创建矩形区域并且预留出border
        bitmapRect.left = borderWidth.toFloat()
        bitmapRect.top = borderWidth.toFloat()
        bitmapRect.right = outWidth - borderWidth.toFloat()
        bitmapRect.bottom = outHeight - borderWidth.toFloat()
//        val rect = RectF(
//            borderWidth.toFloat(),
//            borderWidth.toFloat(),
//            (outWidth - borderWidth).toFloat(),
//            (outHeight - borderWidth).toFloat()
//        )
        //把传入的bitmap绘制到圆角矩形区域内
        canvas.drawRoundRect(bitmapRect, roundRadius.toFloat(), roundRadius.toFloat(), sharedPaint)
        if (borderWidth > 0) {
            //绘制boarder
            canvas.drawRoundRect(
                bitmapRect,
                roundRadius.toFloat(),
                roundRadius.toFloat(),
                borderPaint
            )
        }
        return desBitmap
    }

    override fun setScaleType(scaleType: ScaleType?) {
        super.setScaleType(scaleType)
    }
}
