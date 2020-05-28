package com.devil.library.video.listener;

/**
 * 事件监听
 */
public interface OnPlayerEventListener {
    void onError();

    void onCompletion();

    void onInfo(int what, int extra);

    void onPrepared();

    void onVideoSizeChanged(int width, int height);
}
