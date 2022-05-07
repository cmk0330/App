package com.cmk.app.widget.recyclerview;

/**
 * Created by cuimingkun on 2019/11/21.
 */

public interface Refresh {
    /**
     * 手指拖动中
     *
     * @param height        显示出来的区域高度
     * @param refreshHeight 下拉到触发刷新位置的高度
     * @param totalHeight   总的显示区域高度
     */
    void setHeight(float height, float refreshHeight, float totalHeight);

    /**
     * 触发刷新
     */
    void setRefresh();

    /**
     * 下拉刷新
     */
    void setPullToRefresh();

    /**
     * 释放即可刷新
     */
    void setReleaseToRefresh();
}
