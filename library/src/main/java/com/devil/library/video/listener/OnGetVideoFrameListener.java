package com.devil.library.video.listener;

/**
 * 视频截图回调
 */
public interface OnGetVideoFrameListener {
    void onSuccess(String path,int currentPosition,boolean isFinish);

    void onFailure();
}
