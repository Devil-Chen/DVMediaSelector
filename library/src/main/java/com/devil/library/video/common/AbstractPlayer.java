package com.devil.library.video.common;

import com.devil.library.video.listener.OnPlayerEventListener;

/**
 * 抽象播放器
 */
public abstract class AbstractPlayer implements IMediaPlayer {
    /**
     * 开始渲染视频画面
     */
    public static final int MEDIA_INFO_VIDEO_RENDERING_START = 3;

    /**
     * 视频旋转信息
     */
    public static final int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;

    /**
     * 事件监听者
     */
    protected OnPlayerEventListener mPlayerEventListener;


    @Override
    public void setPlayerEventListener(OnPlayerEventListener listener) {
        this.mPlayerEventListener = listener;
    }


}
