package com.devil.library.video.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import androidx.customview.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.devil.library.video.VideoMediaManager;
import com.devil.library.video.common.AndroidMediaPlayer;
import com.devil.library.video.common.IMediaPlayer;
import com.devil.library.video.listener.OnPlayerEventListener;
import com.devil.library.video.utils.MeasureHelper;

/**
 * 播放器显示view
 */
public class DVVideoView extends LinearLayout {
    private int videoRealW = 1;
    private int videoRealH = 1;
    //垂直视频缩放模式
    private int verticalScaleType = MeasureHelper.SCREEN_SCALE_CENTER_CROP;
    //横向视频缩放模式
    private int horizontalScaleType = MeasureHelper.SCREEN_SCALE_DEFAULT;

    /**
     * 视频播放地址
     */
    private String videoPath;

    /**
     * 播放器
     */
    private IMediaPlayer mediaPlayer;

    /**
     * 渲染的view
     */
    private TextureRenderView renderView;

    /**
     * 跟随手指移动帮助类
     */
    private ViewDragHelper mViewDragHelper;
    /**
     * 缩放手势监听
     */
    private ScaleGestureDetector mScaleGesture;//用与处理双手的缩放手势
    private float mZoomScale = 1.0f;//默认的缩放比为1
    private float maxZoomScale = 2.0f;//最大缩放倍数

    /**
     * 裁剪框坐标及大小{x,y,width,height}
     */
    private float[] cropToolLocation;

    /**
     * 设置是否可拖动
     */
    private boolean isDragEnable;
    //缩放是否启用
    private boolean isZoomEnable;

    public DVVideoView(Context context) {
        super(context);
        initPlayer();
    }

    public DVVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPlayer();
    }

    public DVVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPlayer();
    }

    /**
     * 设置视频地址
     * @param uri
     */
    public void setVideoURI(Uri uri) {
        videoPath = uri.getPath();
        measureSize(videoPath);
        if (renderView != null){
            renderView.setVideoSize(videoRealW,videoRealH);
        }
        if (mediaPlayer != null){
            mediaPlayer.setDataSource(videoPath);
            //确保在渲染view准备好后才开始播放
            if (isRenderViewAlready){
                mediaPlayer.prepareAsync();
            }else {
                isNeedRenderViewAlreadyToPrepare = true;
            }
        }
    }
    /**
     * 设置裁剪框坐标及大小{x,y,width,height}
     * @param cropToolLocation
     */
    public void setCropToolLocation(float[] cropToolLocation){
        this.cropToolLocation = cropToolLocation;
//        renderView.setCropToolLocation(cropToolLocation);
    }
    /**
     * 设置视频地址
     * @param path
     */
    public void setVideoPath(String path){
        videoPath = path;
        measureSize(videoPath);
        if (renderView != null){
            renderView.setVideoSize(videoRealW,videoRealH);
        }
        if (mediaPlayer != null){
            mediaPlayer.setDataSource(videoPath);
            //确保在渲染view准备好后才开始播放
            if (isRenderViewAlready){
                mediaPlayer.prepareAsync();
            }else {
                isNeedRenderViewAlreadyToPrepare = true;
            }

        }
    }

    /**
     * 手动设置视频真实宽高
     * @param width
     * @param height
     */
    public void setVideoRealSize(int width,int height){
        videoRealW = width;
        videoRealH = height;
    }

    /**
     * 获取视频真实宽度
     * @return
     */
    public int getVideoRealWidth(){
        return videoRealW;
    }

    /**
     * 获取视频真实高度
     * @return
     */
    public int getVideoRealHeight(){
        return videoRealH;
    }

    /**
     * 确定宽高
     * @param path
     */
    private void measureSize(String path){
        if (videoRealW > 1 && videoRealH > 1){//手动设置了视频真实宽高，直接设置缩放类型
            if (renderView != null){
                if (videoRealH > videoRealW) {
                    //竖屏视频，使用居中裁剪
                    renderView.setScaleType(verticalScaleType);
                } else {
                    //横屏视频，使用默认模式
                    renderView.setScaleType(horizontalScaleType);
                }
            }
        }else {//没有设置视频真实宽高，先获取视频真实宽高再设置缩放类型
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);
            String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            try {
                videoRealH = Integer.parseInt(height);
                videoRealW = Integer.parseInt(width);
                if (renderView != null){
                    if (videoRealH > videoRealW) {
                        //竖屏视频，使用居中裁剪
                        renderView.setScaleType(verticalScaleType);
                    } else {
                        //横屏视频，使用默认模式
                        renderView.setScaleType(horizontalScaleType);
                    }
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            retriever.release();
        }

    }
    /**
     * 设置视频宽高比，缩放模式使用SCREEN_SCALE_BY_SELF才有效
     */
    public void setVideoRatio(int videoWidthRatio,int videoHeightRatio){
        renderView.setVideoRatio(videoWidthRatio,videoHeightRatio);
    }
    /**
     * 垂直视频缩放模式
     * @param scaleType MeasureHelper中的scaleType类型
     */
    public void setVerticalScaleType(int scaleType){
        verticalScaleType = scaleType;
    }

    /**
     * 垂直视频缩放模式
     * @return
     */
    public int getVerticalScaleType(){
        return verticalScaleType;
    }

    /**
     * 横向视频缩放模式
     * @param scaleType MeasureHelper中的scaleType类型
     */
    public void setHorizontalScaleType(int scaleType){
        horizontalScaleType = scaleType;
    }

    /**
     * 横向视频缩放模式
     * @return
     */
    public int getHorizontalScaleType(){
        return horizontalScaleType;
    }

    /**
     * 获取原视频与现在大小的大小比
     * @return
     */
    public float[] getSizeRatio(){
        return renderView.getSizeRatio();
    }

    /**
     * 初始化播放器
     */
    private void initPlayer(){
        if (mediaPlayer == null){
            //获取自定义播放器
            mediaPlayer = VideoMediaManager.getInstance().getMediaPlayer();
            //如果没有设置播放器，使用默认播放器
            if (mediaPlayer == null){
                mediaPlayer = new AndroidMediaPlayer(getContext());
            }
        }
        if (renderView == null){
            renderView = new TextureRenderView(getContext());
        }

        mediaPlayer.initPlayer();
        renderView.attachToPlayer(mediaPlayer);
        renderView.setOnPreparedListener(preparedListener);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(renderView.getView(), params);

    }
    /**
     * 设置是否启用缩放
     * @param isZoomEnable
     */
    public void setZoomEnable(boolean isZoomEnable){
        this.isZoomEnable = isZoomEnable;
        if (isZoomEnable){
            if (mScaleGesture == null){
                mScaleGesture = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
                    //随着手势操作，回调的方法，
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        float previousSpan = detector.getPreviousSpan();//缩放发生前的两点距离
                        float currentSpan = detector.getCurrentSpan();//缩放发生时的两点距离
                        float sourceZoomScale = mZoomScale;
                        if (previousSpan < currentSpan)//放大
                        {
                            mZoomScale = mZoomScale + (currentSpan - previousSpan) / previousSpan;
                        } else {
                            mZoomScale = mZoomScale - (previousSpan - currentSpan) / previousSpan;
                        }
                        //确保放大最多为maxZoomScale倍，最少不能小于原本
                        if (mZoomScale > maxZoomScale) {
                            mZoomScale = maxZoomScale;
                        } else if (mZoomScale < 1) {
                            mZoomScale = 1;
                        }
                        if (sourceZoomScale == mZoomScale){//如果和当前缩放倍数一样不做改变
                            return true;
                        }
                        //在这里调用进行缩放，虽然控件显示大小改变了，但是在ViewDragHelper的回调方法中获得的View的getWidth（）和getHeight（）是原来的大小，不会发生改变
//                        renderView.setScaleX(mZoomScale);
//                        renderView.setScaleY(mZoomScale);

                        //重绘布局
                        renderView.setZoomScale(mZoomScale);
                        renderView.requestLayout();
//                        PointF targetPoint = scaleByPoint(sourcePoint.x,sourcePoint.y,detector.getFocusX(),detector.getFocusY(),mZoomScale);
                        return true;
                    }
//                    PointF sourcePoint;
                    @Override
                    public boolean onScaleBegin(ScaleGestureDetector detector) {
//                        sourcePoint = new PointF(renderView.getX(),renderView.getY());
                        return true;
                    }

                    /**

                     * @param detector
                     */
                    @Override
                    public void onScaleEnd(ScaleGestureDetector detector) {

                    }
                });
            }

        }else {
            mScaleGesture = null;
        }
    }

    /**
     * 一个坐标点，以某个点为缩放中心，缩放指定倍数，求这个坐标点在缩放后的新坐标值。
     * @param targetPointX 坐标点的X
     * @param targetPointY 坐标点的Y
     * @param scaleCenterX 缩放中心的X
     * @param scaleCenterY 缩放中心的Y
     * @param scale 缩放倍数
     * @return 坐标点的新坐标
     */
    private PointF scaleByPoint(float targetPointX,float targetPointY,float scaleCenterX,float scaleCenterY,float scale){
        Matrix matrix = new Matrix();
        // 将Matrix移到到当前圆所在的位置，
        // 然后再以某个点为中心进行缩放
        matrix.preTranslate(targetPointX,targetPointY);
        matrix.postScale(scale,scale,scaleCenterX,scaleCenterY);
        float[] values = new float[9];
        matrix.getValues(values);
        return new PointF(values[Matrix.MTRANS_X],values[Matrix.MTRANS_Y]);
    }
    /**
     * 设置是否可拖动画面
     * @param isDragEnable 是否可拖动
     */
    public void setDragEnable(boolean isDragEnable){
        this.isDragEnable = isDragEnable;
        if (isDragEnable){
            if (mViewDragHelper == null){
                mViewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {

                    private int mLeft;
                    private int mTop;

                    @Override
                    public boolean tryCaptureView(View child, int pointerId) {
                        return true;
                    }

                    @Override
                    public void onViewCaptured(View capturedChild, int activePointerId) {
                        super.onViewCaptured(capturedChild, activePointerId);
                        mLeft = capturedChild.getLeft();
                        mTop = capturedChild.getTop();
                    }

                    @Override
                    public int clampViewPositionVertical(View child, int top, int dy) {
                        mTop = top;
                        return mTop;
                    }

                    @Override
                    public int clampViewPositionHorizontal(View child, int left, int dx) {
                        mLeft = left;
                        return mLeft;

                    }

                    @Override
                    public void onViewReleased(View releasedChild, float xvel, float yvel) {
                        super.onViewReleased(releasedChild, xvel, yvel);
                        int cropToolX = (int) cropToolLocation[0];
                        int cropToolY = (int) cropToolLocation[1];
                        int cropToolWidth = (int) cropToolLocation[2];
                        int cropToolHeight = (int) cropToolLocation[3];
                        int renderWidth = renderView.getWidth();
                        int renderHeight = renderView.getHeight();


                        int finalLeft = mLeft;
                        int finalTop = mTop;
                        //计算顶部位置
                        if (mTop > cropToolY ){
                            //顶部位置在裁剪框下，把顶部放回与裁剪框顶部对齐
                            finalTop = cropToolY;
                        }else if(mTop < cropToolY && (mTop + renderHeight) < (cropToolY + cropToolHeight)){
                            //顶部位置在裁剪框下，但画面高度不足以铺满整个裁剪框，把画面底部与裁剪框底部对齐
                            finalTop= (cropToolY + cropToolHeight) - renderHeight;
                        }
                        //计算左边位置
                        if(mLeft > cropToolX){
                            //左边位置大于裁剪框左边位置，回弹到裁剪框左边位置
                            finalLeft = cropToolX;
                        }else if(mLeft < cropToolX && (mLeft + renderWidth) < (cropToolX + cropToolWidth)){
                            //左边位置小于裁剪框左边位置，但画面宽度不足以铺满整个裁剪框，回弹到裁剪框右边位置
                            finalLeft = (cropToolX + cropToolWidth) - renderWidth;
                        }

                        mViewDragHelper.settleCapturedViewAt( finalLeft,  finalTop);
                        invalidate();
                    }
                });
            }
        }else{
            mViewDragHelper = null;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isDragEnable){
            return  mViewDragHelper.shouldInterceptTouchEvent(ev);
        }else{
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isDragEnable){
            mViewDragHelper.processTouchEvent(event);
        }
        if (isZoomEnable){
            return mScaleGesture.onTouchEvent(event);
        }else{
            if (isDragEnable){
                return true;
            }
            return false;
        }

    }


    @Override
    public void computeScroll() {
        if (mViewDragHelper != null && mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    /**
     * 设置最大缩放比
     * @param maxSize
     */
    public void setScaleMaxSize(float maxSize){
        this.maxZoomScale = maxSize;
    }




    /**
     * 设置是否循环播放
     * @param isLoop
     */
    public void setLoop(boolean isLoop){
        if (mediaPlayer != null){
            mediaPlayer.setLoop(isLoop);
        }
    }

    /**
     * 确保在渲染view准备好后才开始播放
     */
    private boolean isNeedRenderViewAlreadyToPrepare = false;
    private boolean isRenderViewAlready = false;
    private TextureRenderView.OnPreparedListener preparedListener = new TextureRenderView.OnPreparedListener() {
        @Override
        public void onPrepared() {
            isRenderViewAlready = true;
            if (isNeedRenderViewAlreadyToPrepare){
                if (mediaPlayer != null){
                    mediaPlayer.prepareAsync();
                }
            }
        }
    };

    /**
     * 获取RenderLocation
     * @return
     */
    public int[] getRenderLocation(){
        int[] location = new int[2];
        renderView.getLocationOnScreen(location);
//        int x = location[0]; // view距离 屏幕左边的距离（即x轴方向）
//        int y = location[1]; // view距离 屏幕顶边的距离（即y轴方向）
        return location;
    }
    /**
     * 获取本view Location
     * @return
     */
    public int[] getLocation(){
        int[] location = new int[2];
        getLocationOnScreen(location);
//        int x = location[0]; // view距离 屏幕左边的距离（即x轴方向）
//        int y = location[1]; // view距离 屏幕顶边的距离（即y轴方向）
        return location;
    }

    /**
     * 获取Render大小
     * @return
     */
    public int[] getRenderSize(){
        int[] size = new int[2];
        size[0] = renderView.getWidth();
        size[1] = renderView.getHeight();
        return size;
    }

    /**
     * 获取本view大小
     * @return
     */
    public int[] getSize(){
        int[] size = new int[2];
        size[0] = getWidth();
        size[1] = getHeight();
        return size;
    }

    /**
     * 设置视频地址
     * @param path
     */
    public void setDataSource(String path){
        if (mediaPlayer != null){
            mediaPlayer.setDataSource(path);
        }
    }

    /**
     * 获取视频时长
     * @return
     */
    public long getDuration(){
        if (mediaPlayer != null){
            return  mediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 获取当前位置
     * @return
     */
    public long getCurrentPosition(){
        if (mediaPlayer != null){
            return  mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 暂停
     */
    public void pause(){
        if (mediaPlayer != null){
            mediaPlayer.pause();
        }
    }

    /**
     * 是否正在播放
     * @return
     */
    public boolean isPlaying(){
        if (mediaPlayer != null){
            return  mediaPlayer.isPlaying();
        }
        return false;
    }

    /**
     * 开始播放
     */
    public void start(){
        if (mediaPlayer != null){
            mediaPlayer.start();
        }
    }

    /**
     * 调整进度
     */
    public void seekTo(long time){
        if (mediaPlayer != null){
            mediaPlayer.seekTo(time);
        }
    }

    /**
     * 设置事件监听
     */
    public void setPlayerEventListener(OnPlayerEventListener listener){
        if (mediaPlayer != null){
            mediaPlayer.setPlayerEventListener(listener);
        }
    }



    /**
     * 释放资源
     */
    public void release(){
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (renderView != null){
            renderView.release();
            renderView = null;
        }
    }
}