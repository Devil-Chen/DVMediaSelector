package com.devil.library.media.utils;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;

/**
 * 生产LayoutManager工厂
 */
public class LayoutManagerHelper {
    /**
     * 获取LinearLayoutManager
     * @param mContext 上下文
     * @param orientation 方向 （LinearLayoutManager.VERTICAL、LinearLayoutManager.HORIZONTAL）
     * @return LinearLayoutManager
     */
    public static LinearLayoutManager getLinearLayoutManager(Context mContext, int orientation){
        //布局管理器对象 参数1.上下文 2.规定一行显示几列的参数常量
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        //设置RecycleView显示的方向是水平还是垂直
        linearLayoutManager.setOrientation(orientation);
        return linearLayoutManager;
    }

    /**
     * 获取GridLayoutManager
     * @param mContext 上下文
     * @param spanCount 每行显示数量
     * @param orientation 方向（GridLayout.VERTICAL、GridLayout.HORIZONTAL）
     * @return GridLayoutManager
     */
    public static GridLayoutManager getGridLayoutManager(Context mContext, int spanCount, int orientation){
        //布局管理器对象 参数1.上下文 2.规定一行显示几列的参数常量
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, spanCount);
        //设置RecycleView显示的方向是水平还是垂直 GridLayout.HORIZONTAL水平  GridLayout.VERTICAL默认垂直
        gridLayoutManager.setOrientation(orientation);
        return gridLayoutManager;
    }
}
