package com.devil.library.video.utils;

import android.view.View;

/**
 * 视频缩放比帮助类
 */
public class MeasureHelper {
    //缩放模式
    public static final int SCREEN_SCALE_DEFAULT = 0;
    public static final int SCREEN_SCALE_16_9 = 1;
    public static final int SCREEN_SCALE_4_3 = 2;
    public static final int SCREEN_SCALE_1_1 = 3;
    public static final int SCREEN_SCALE_MATCH_PARENT = 4;
    public static final int SCREEN_SCALE_ORIGINAL = 5;
    public static final int SCREEN_SCALE_CENTER_CROP = 6;
    public static final int SCREEN_SCALE_BY_SELF = 7;//自定义宽高比
    //是否保持视频原始宽高比
    private boolean isKeepVideoOriginalRatio = true;
    //视频真实宽度
    private float mVideoWidth;
    //视频真实高度
    private float mVideoHeight;
    //当前的缩放模式
    private int mCurrentScreenScale;
    //视频旋转角度
    private int mVideoRotationDegree;

    //缩放后与原视频宽度比
    private float widthRatio = 1;
    //缩放后与原视频高度比
    private float heightRatio = 1;
    /**
     * 设置缩放比（触摸缩放比）
     */
    private float mZoomScale = 1.0f;
    //使用自定义宽高比，默认视频宽高比16:9
    private float videoWidthRatio = 16f;
    //使用自定义宽高比，默认视频宽高比16:9
    private float videoHeightRatio = 9f;

    /**
     * 设置视频旋转角度
     * @param videoRotationDegree
     */
    public void setVideoRotation(int videoRotationDegree) {
        mVideoRotationDegree = videoRotationDegree;
    }

    /**
     * 设置视频真实大小
     * @param width
     * @param height
     */
    public void setVideoSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
    }

    /**
     * 设置缩放比（触摸缩放比）
     * @param mZoomScale
     */
    public void setZoomScale(float mZoomScale){
        this.mZoomScale = mZoomScale;
    }

    /**
     * 设置视频缩放模式
     * @param screenScale
     */
    public void setScreenScale(int screenScale) {
        mCurrentScreenScale = screenScale;
    }

    /**
     * 设置当前视频缩放模式
     * @return
     */
    public int getCurrentScreenScale(){
        return mCurrentScreenScale;
    }

    /**
     * 注意：VideoView的宽高一定要定死，否者以下算法不成立
     */
    public int[] doMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) { // 软解码时处理旋转信息，交换宽高
            widthMeasureSpec = widthMeasureSpec + heightMeasureSpec;
            heightMeasureSpec = widthMeasureSpec - heightMeasureSpec;
            widthMeasureSpec = widthMeasureSpec - heightMeasureSpec;
        }

        float width = View.MeasureSpec.getSize(widthMeasureSpec);
        float height = View.MeasureSpec.getSize(heightMeasureSpec);

        if (mVideoHeight == 0 || mVideoWidth == 0) {
            return new int[]{(int) width, (int) height};
        }

        //如果设置了比例
        switch (mCurrentScreenScale) {
            case SCREEN_SCALE_DEFAULT:
            default:
                if (mVideoWidth * height < width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    height = width * mVideoHeight / mVideoWidth;
                }
                break;
            case SCREEN_SCALE_ORIGINAL:
                width = mVideoWidth;
                height = mVideoHeight;
                break;
            case SCREEN_SCALE_16_9:
                if (height > width / 16 * 9) {
                    height = width / 16 * 9;
                    width = getCorrectWidth(width,height);
                } else {
                    width = height / 9 * 16;
                    height = getCorrectHeight(width,height);
                }
                break;
            case SCREEN_SCALE_4_3:
                if (height > width / 4 * 3) {
                    height = width / 4 * 3;
                    width = getCorrectWidth(width,height);
                } else {
                    width = height / 3 * 4;
                    height = getCorrectHeight(width,height);
                }
                break;
            case SCREEN_SCALE_MATCH_PARENT:
                width = widthMeasureSpec;
                height = heightMeasureSpec;
                break;
            case SCREEN_SCALE_CENTER_CROP:
                if (mVideoWidth * height > width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight;
                } else {
                    height = width * mVideoHeight / mVideoWidth;
                }
                break;
            case SCREEN_SCALE_1_1:
                if (height > width ) {
                    height = width * 1;
                } else {
                    width = height * 1;
                }
                break;
            case SCREEN_SCALE_BY_SELF:
                if (height > width / videoWidthRatio * videoHeightRatio) {
                    height = width / videoWidthRatio * videoHeightRatio;
                    width = getCorrectWidth(width,height);
                } else {
                    width = height / videoHeightRatio * videoWidthRatio;
                    height = getCorrectHeight(width,height);
                }
                break;
        }
        width *= mZoomScale;
        height *= mZoomScale;
        //更新与真实视频宽高比
        widthRatio = mVideoWidth / width;
        heightRatio = mVideoHeight / height;

        return new int[]{(int) width, (int) height};
    }

    /**
     * 根据高度比，获取显示的正确视频宽度
     * @return
     */
    private float getCorrectWidth(float width,float height){
       float finalWidth = 0;
       if (isKeepVideoOriginalRatio){
           finalWidth = mVideoWidth * (height / mVideoHeight);
       }else{
           finalWidth = width;
       }
       return finalWidth;
    }

    /**
     * 根据高度比，获取显示的正确视频高度
     * @return
     */
    private float getCorrectHeight(float width,float height){
        float finalHeight = 0;
        if (isKeepVideoOriginalRatio){
            finalHeight = mVideoHeight * (width / mVideoWidth);
        }else{
            finalHeight = height;
        }
        return finalHeight;
    }

    /**
     * 是否保持原视频宽高比例显示（默认true，为false时视频显示可能会拉伸）
     */
    public void setKeepVideoOriginalRatio(boolean isKeepVideoOriginalRatio){
        this.isKeepVideoOriginalRatio = isKeepVideoOriginalRatio;
    }

    /**
     * 设置视频宽高比，缩放模式使用SCREEN_SCALE_BY_SELF才有效
     */
    public void setVideoRatio(int videoWidthRatio,int videoHeightRatio){
        this.videoWidthRatio = videoWidthRatio;
        this.videoHeightRatio = videoHeightRatio;
    }

    /**
     * 获取原视频与现在大小的大小比
     * @return
     */
    public float[] getSizeRatio(){
        return new float[]{widthRatio,heightRatio};
    }
}
