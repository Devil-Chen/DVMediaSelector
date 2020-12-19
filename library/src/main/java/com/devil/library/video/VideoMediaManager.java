package com.devil.library.video;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.devil.library.media.utils.FileUtils;
import com.devil.library.video.bean.VideoCropFrameInfo;
import com.devil.library.video.common.IMediaPlayer;
import com.devil.library.video.listener.OnGetVideoFrameListener;
import com.devil.library.video.listener.OnVideoTrimListener;
import com.devil.library.video.ui.DVVideoCropActivity;
import com.devil.library.video.ui.DVVideoTrimActivity;


import java.util.LinkedList;

import VideoHandle.EpEditor;
import VideoHandle.OnEditorListener;

/**
 * 视频相关管理者
 */
public class VideoMediaManager {
    //单例
    private static VideoMediaManager instance;
    //handler
    private static Handler mainHandler = null;
    //视频截图的任务队列
    private static LinkedList<VideoCropFrameInfo> videoFrameTaskList = null;

    //播放器
    private IMediaPlayer mediaPlayer;

    /**
     * 获取单例
     * @return
     */
    public static VideoMediaManager getInstance() {
        if (instance == null) {
            synchronized (VideoMediaManager.class) {
                if (instance == null) {
                    instance = new VideoMediaManager();
                }
            }
        }
        return instance;
    }


    /**
     * 设置自定义播放器
     * @param mediaPlayer
     */
    public void setMediaPlayer(IMediaPlayer mediaPlayer){
        this.mediaPlayer = mediaPlayer;
    }

    /**
     * 获取自定义播放器
     */
    public IMediaPlayer getMediaPlayer(){
        return this.mediaPlayer ;
    }

    //初始化handler
    private static void initMainHandler(){
        if (mainHandler == null){
            mainHandler = new Handler(Looper.getMainLooper());
        }
    }

    /**
     * 使用ffmpeg获取视频某个帧截图（必须回调后才能继续调用）
     * @param videoPath 视频地址
     * @param savePath 图片保存地址
     * @param frameTime 截取的时间点（毫秒）
     */
    public static void getVideoFrame(String videoPath,String savePath,long frameTime,OnGetVideoFrameListener listener){
        EpEditor.videoFrame(videoPath, savePath, frameTime, new OnEditorListener() {
            @Override
            public void onSuccess() {
                initMainHandler();
                mainHandler.post(()->{
                    if (listener != null){
                        listener.onSuccess(savePath,0,true);
                    }

            });
            }

            @Override
            public void onFailure() {
                initMainHandler();
                mainHandler.post(()->{
                    if (listener != null){
                        listener.onFailure();
                    }
                });
            }

            @Override
            public void onProgress(float v) {

            }
        });
    }


    /**
     * 使用ffmpeg获取视频某个帧截图（必须所有回调后才能继续调用）
     * @param videoPath 视频地址
     * @param saveDir 图片保存目录
     * @param frameTimeArray 截取的时间点（毫秒）
     */
    public static void getVideoFrameArray(String videoPath,String saveDir,long[] frameTimeArray,OnGetVideoFrameListener listener){
        FileUtils.createDir(saveDir);

        if (videoFrameTaskList == null){
            videoFrameTaskList = new LinkedList<VideoCropFrameInfo>();
            videoFrameTaskList.addAll(VideoCropFrameInfo.timeArray2Info(videoPath,saveDir,frameTimeArray));
            startVideoCropFrameTask(listener);
        }else{
            Log.e("VideoMediaManager","请等待视频截图完成后再继续调用！");
        }


    }

    //当前截取视频截图的位置
    private static int currentFrameTimePosition;
    /**
     * 开始获取视频截图
     * @param listener
     */
    private static void startVideoCropFrameTask(OnGetVideoFrameListener listener){

        VideoCropFrameInfo info = videoFrameTaskList.getFirst();
        EpEditor.videoFrame(info.videoPath, info.savePath, info.cropFrameTime, new OnEditorListener() {
            @Override
            public void onSuccess() {
                initMainHandler();
                mainHandler.post(()->{
                    if (listener != null){
                        listener.onSuccess(info.savePath,currentFrameTimePosition,videoFrameTaskList.size() == 1);
                    }
                    currentFrameTimePosition += 1;
                    //移除第一个数据
                    videoFrameTaskList.removeFirst();
                    if (videoFrameTaskList.size() == 0){
                        videoFrameTaskList = null;
                        currentFrameTimePosition = 0;
                    }else{
                        //继续获取截图
                        startVideoCropFrameTask(listener);
                    }
                });

            }

            @Override
            public void onFailure() {
                initMainHandler();
                mainHandler.post(()->{
                    if (listener != null){
                        listener.onFailure();
                    }
                });
            }

            @Override
            public void onProgress(float v) {

            }
        });

    }


    /**
     * 开启视频剪辑界面
     * @param mActivity 上下文
     * @param videoPath 视频地址
     * @param savePath 保存地址
     * @param listener 剪辑监听
     */
    public static void openVideoTrimActivity(Activity mActivity, String videoPath, String savePath, OnVideoTrimListener listener){
        Intent intent = new Intent(mActivity,DVVideoTrimActivity.class);
        intent.putExtra("videoPath",videoPath);
        intent.putExtra("savePath",savePath);

        if (listener != null){
            DVVideoTrimActivity.videoTrimListener = listener;
        }

        mActivity.startActivity(intent);
    }

    /**
     * 开启视频剪辑界面
     * @param mActivity 上下文
     * @param videoPath 视频地址
     * @param savePath 保存地址
     * @param frameRate 输出视频帧率
     * @param bitRate 输出视频码率
     * @param listener 剪辑监听
     */
    public static void openVideoTrimActivity(Activity mActivity, String videoPath, String savePath,int frameRate,int bitRate, OnVideoTrimListener listener){
        Intent intent = new Intent(mActivity,DVVideoTrimActivity.class);
        intent.putExtra("videoPath",videoPath);
        intent.putExtra("savePath",savePath);
        intent.putExtra("frameRate",frameRate);
        intent.putExtra("bitRate",bitRate);

        if (listener != null){
            DVVideoTrimActivity.videoTrimListener = listener;
        }

        mActivity.startActivity(intent);
    }

    /**
     * 开启视频剪辑界面
     * @param mActivity 上下文
     * @param videoPath 视频地址
     * @param savePath 保存地址
     * @param frameRate 输出视频帧率
     * @param bitRate 输出视频码率
     * @param backTitle 返回按钮文字
     * @param sureTitle 确定按钮文字
     * @param tipText 设置进度上方提示文字
     * @param listener 剪辑监听
     */
    public static void openVideoTrimActivity(Activity mActivity, String videoPath, String savePath,int frameRate,int bitRate,String backTitle,String sureTitle,String tipText, OnVideoTrimListener listener){
        Intent intent = new Intent(mActivity,DVVideoTrimActivity.class);
        intent.putExtra("videoPath",videoPath);
        intent.putExtra("savePath",savePath);
        intent.putExtra("frameRate",frameRate);
        intent.putExtra("bitRate",bitRate);
        intent.putExtra("backTitle",backTitle);
        intent.putExtra("sureTitle",sureTitle);
        intent.putExtra("tipText",tipText);

        if (listener != null){
            DVVideoTrimActivity.videoTrimListener = listener;
        }

        mActivity.startActivity(intent);
    }

    /**
     * 开启视频裁剪界面
     * @param mActivity 上下文
     * @param videoPath 视频地址
     * @param savePath 保存地址
     * @param listener 剪辑监听
     */
    public static void openVideoCropActivity(Activity mActivity, String videoPath, String savePath, OnVideoTrimListener listener){
        Intent intent = new Intent(mActivity,DVVideoCropActivity.class);
        intent.putExtra("videoPath",videoPath);
        intent.putExtra("savePath",savePath);
        if (listener != null){
            DVVideoCropActivity.videoTrimListener = listener;
        }

        mActivity.startActivity(intent);
    }

    /**
     * 开启视频裁剪界面
     * @param mActivity 上下文
     * @param videoPath 视频地址
     * @param savePath 保存地址
     * @param widthRatio 宽高比中的宽
     * @param heightRatio 宽高比中的高
     * @param listener 剪辑监听
     */
    public static void openVideoCropActivity(Activity mActivity, String videoPath, String savePath,int widthRatio,int heightRatio, OnVideoTrimListener listener){
        Intent intent = new Intent(mActivity,DVVideoCropActivity.class);
        intent.putExtra("videoPath",videoPath);
        intent.putExtra("savePath",savePath);
        intent.putExtra("widthRatio",widthRatio);
        intent.putExtra("heightRatio",heightRatio);
        if (listener != null){
            DVVideoCropActivity.videoTrimListener = listener;
        }

        mActivity.startActivity(intent);
    }

    /**
     * 开启视频裁剪界面
     * @param mActivity 上下文
     * @param videoPath 视频地址
     * @param savePath 保存地址
     * @param widthRatio 宽高比中的宽
     * @param heightRatio 宽高比中的高
     * @param backTitle 返回按钮文字
     * @param sureTitle 确定按钮文字
     * @param listener 剪辑监听
     */
    public static void openVideoCropActivity(Activity mActivity, String videoPath, String savePath,int widthRatio,int heightRatio,String backTitle,String sureTitle, OnVideoTrimListener listener){
        Intent intent = new Intent(mActivity,DVVideoCropActivity.class);
        intent.putExtra("videoPath",videoPath);
        intent.putExtra("savePath",savePath);
        intent.putExtra("widthRatio",widthRatio);
        intent.putExtra("heightRatio",heightRatio);
        intent.putExtra("backTitle",backTitle);
        intent.putExtra("sureTitle",sureTitle);
        if (listener != null){
            DVVideoCropActivity.videoTrimListener = listener;
        }

        mActivity.startActivity(intent);
    }

}
