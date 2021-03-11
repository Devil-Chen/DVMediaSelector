package com.devil.library.video.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import com.devil.library.video.common.IMediaPlayer;
import com.devil.library.video.utils.MeasureHelper;

/**
 * 视频显示的TextureView
 */
public class TextureRenderView extends TextureView implements TextureView.SurfaceTextureListener {
    private MeasureHelper mMeasureHelper;
    private SurfaceTexture mSurfaceTexture;
    private OnPreparedListener preparedListener;
    @Nullable
    private IMediaPlayer mMediaPlayer;
    private Surface mSurface;
    /**
     * 设置缩放比（触摸缩放比）
     */
    private float mZoomScale = 1.0f;
    public TextureRenderView(Context context) {
        super(context);

    }

    {
        mMeasureHelper = new MeasureHelper();
        setSurfaceTextureListener(this);
    }

    public void setOnPreparedListener(OnPreparedListener listener){
        this.preparedListener = listener;
    }

    public void attachToPlayer(@NonNull IMediaPlayer player) {
        this.mMediaPlayer = player;
    }

    /**
     * 设置视频真实大小
     * @param videoWidth
     * @param videoHeight
     */
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight);
            requestLayout();
        }
    }

    /**
     * 设置缩放比（触摸缩放比）
     * @param mZoomScale
     */
    public void setZoomScale(float mZoomScale){
        this.mZoomScale = mZoomScale;
        mMeasureHelper.setZoomScale(mZoomScale);
    }

    public void setVideoRotation(int degree) {
        mMeasureHelper.setVideoRotation(degree);
        setRotation(degree);
    }
    /**
     * 获取原视频与现在大小的大小比
     * @return
     */
    public float[] getSizeRatio(){
       return mMeasureHelper.getSizeRatio();
    }

    public void setScaleType(int scaleType) {
        mMeasureHelper.setScreenScale(scaleType);
        requestLayout();
    }
    /**
     * 设置视频宽高比，缩放模式使用SCREEN_SCALE_BY_SELF才有效
     */
    public void setVideoRatio(int videoWidthRatio,int videoHeightRatio){
        mMeasureHelper.setVideoRatio(videoWidthRatio,videoHeightRatio);
    }

    public View getView() {
        return this;
    }


    public Bitmap doScreenShot() {
        return getBitmap();
    }


    public void release() {
        if (mSurface != null)
            mSurface.release();

        if (mSurfaceTexture != null)
            mSurfaceTexture.release();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] measuredSize = mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measuredSize[0],measuredSize[1]);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        if (mSurfaceTexture != null) {
            setSurfaceTexture(mSurfaceTexture);
        } else {
            mSurfaceTexture = surfaceTexture;
            mSurface = new Surface(surfaceTexture);
            if (mMediaPlayer != null) {
                mMediaPlayer.setSurface(mSurface);
            }
        }
        if (preparedListener != null){
            preparedListener.onPrepared();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    /**
     * 是否准备好监听
     */
    public interface OnPreparedListener{
        public void onPrepared();
    }
}