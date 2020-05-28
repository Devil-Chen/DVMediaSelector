package com.devil.library.video.utils;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * 可设置是否可滚动的LinearLayoutManager
 */
public class DVLinearLayoutManager extends LinearLayoutManager{
    //是否可滚动
    private boolean canScroll = true;

    public DVLinearLayoutManager(Context context) {
        super(context);
    }

    public DVLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public DVLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 垂直方向
     * @return
     */
    @Override
    public boolean canScrollVertically() {
        return canScroll && super.canScrollVertically();
    }

    /**
     * 水平方向
     * @return
     */
    @Override
    public boolean canScrollHorizontally() {
        return canScroll && super.canScrollHorizontally();
    }

    /**
     * 设置是否可以滑动
     * @param canScroll
     */
    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

}
