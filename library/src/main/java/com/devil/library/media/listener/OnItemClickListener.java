package com.devil.library.media.listener;

import com.devil.library.media.bean.MediaInfo;

import java.util.ArrayList;

/**
 * 菜单点击回调
 */
public interface OnItemClickListener{
    /**
     * 菜单点击事件
     * @param li_AllInfo
     * @param position
     */
    void onItemClick(ArrayList<MediaInfo> li_AllInfo, int position);

    /**
     * 资源选择事件
     * @param info
     */
    void onItemCheck(MediaInfo info,boolean isChecked);

    /**
     * 子菜单是否可选择
     * @param position
     */
    boolean itemCheckEnabled(int position,boolean isChecked);
}
