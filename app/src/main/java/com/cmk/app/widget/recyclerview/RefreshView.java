package com.cmk.app.widget.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

/**
 * Created by cuimingkun on 2019/11/21.
 */

public abstract class RefreshView extends RelativeLayout implements Refresh {
    public RefreshView(Context context) {
        this(context, null);
    }

    public RefreshView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.CENTER);
    }

    /**
     * 释放即可到达二楼
     */
    public abstract void setReleaseToSecondFloor();

    /**
     * 展示二楼
     */
    public abstract void setToSecondFloor();

    /**
     * 回到一楼
     */
    public abstract void setToFirstFloor();
}
