package com.devil.library.video.common;

import android.view.Surface;
import android.view.SurfaceHolder;

import com.devil.library.video.listener.OnPlayerEventListener;

/**
 * 用于裁剪的简单播放器
 */
public interface IMediaPlayer {

    /**
     * 初始化播放器
     */
    public void initPlayer();

    /**
     * 设置视频地址
     * @param path
     */
    public void setDataSource(String path);

    /**
     * 设置渲染视频的View,主要用于TextureView
     */
    public  void setSurface(Surface surface);

    /**
     * 设置渲染视频的View,主要用于SurfaceView
     */
    public  void setDisplay(SurfaceHolder holder);

    /**
     * 获取视频时长
     * @return
     */
    public long getDuration();

    /**
     * 当前播放的位置
     * @return
     */
    public long getCurrentPosition();

    /**
     * 准备开始播放（异步）
     */
    public  void prepareAsync();

    /**
     * 暂停
     */
    public void pause();

    /**
     * 停止
     */
    public void stop();

    /**
     * 开始播放
     */
    public void start();

    /**
     * 重置
     */
    public void reset();

    /**
     * 设置循环
     * @param isLoop
     */
    public void setLoop(boolean isLoop);

    /**
     * 是否正在播放
     * @return
     */
    public boolean isPlaying();

    /**
     * 调整进度
     */
    public void seekTo(long time);

    /**
     * 设置事件监听
     */
    public void setPlayerEventListener(OnPlayerEventListener listener);

    /**
     * 释放资源
     */
    public void release();

}
