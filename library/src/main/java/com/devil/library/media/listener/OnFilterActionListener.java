package com.devil.library.media.listener;

import com.cgfay.filter.glfilter.resource.bean.ResourceData;

public interface OnFilterActionListener extends OnAdjustChangeListener{
    /**
     * 关闭滤镜工具
     */
    public void onCloseFilter();

    /**
     * 颜色滤镜改变
     */
    public void onColorFilterChanged(ResourceData resourceData);
}
