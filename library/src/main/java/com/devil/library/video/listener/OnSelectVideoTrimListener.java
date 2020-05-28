package com.devil.library.video.listener;

/**
 * 视频裁剪时间选择后回调
 */
public interface OnSelectVideoTrimListener {
    /**
     * 取消选择
     */
    void onCancelSelect();
    /**
     * 选择好视频长度回调
     * @param videoPath 视频长度
     * @param startTime 裁剪开始时间
     * @param endTime 裁剪结束时间
     * @param mDuration 视频总时长
     */
    void onAlreadySelect(String videoPath,long startTime,long endTime,long mDuration);
}
