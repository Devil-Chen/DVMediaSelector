package com.devil.library.video.listener;

/**
 * 视频裁剪完成后回调
 */
public interface OnVideoTrimListener {
    /**
     * 视频裁剪完成
     * @param savePath 视频保存路径
     */
    public void onVideoTrimSuccess(String savePath);

    /**
     * 视频裁剪失败
     */
    public void onVideoTrimError(String msg);

    /**
     * 取消视频裁剪
     */
    public void onVideoTrimCancel();

    /**
     * 视频裁剪进度0-1
     */
    public void onVideoTrimProgress(float progress);
}
