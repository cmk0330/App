package com.cmk.app.widget;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.cmk.app.R;

/**
 * Created by cmk on 2018/11/23.
 */

public class CircleView extends View {

    private int circleColor;

    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        try {
            circleColor = typedArray.getColor(R.styleable.CircleView_circleColor, Color.BLACK);
        } finally {
            typedArray.recycle();
        }
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getSize(100, widthMeasureSpec);
        int height = getSize(100, heightMeasureSpec);

        width = height;

        setMeasuredDimension(width, height);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(circleColor);

        int r = getMeasuredWidth() / 2;
        canvas.drawCircle(getPaddingStart() + r, getPaddingTop() + r, r, paint);
    }

    private int getSize(int defaultSize, int measureSpec) {
        int size = 0;
        int measureMode = MeasureSpec.getMode(measureSpec);
        int measureSize = MeasureSpec.getSize(measureSpec);
        switch (measureMode) {
            case MeasureSpec.UNSPECIFIED: {//如果没有指定大小，就设置为默认大小   客源分配   （您暂未获得授权）
                size = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {//如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                size = measureSize;
                break;
            }
            case MeasureSpec.EXACTLY: {//如果是固定的大小，那就不要去改变它
                size = measureSize;
                break;
            }
            default:
                break;
        }
        return size;
    }
}