package com.devil.library.media.view;

import android.content.Context;

import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.ViewGroup;


import com.cgfay.filter.widget.GLImageSurfaceView;
import com.devil.library.video.utils.MeasureHelper;

/**
 * 可设置缩放模式的SurfaceView
 */
public class DVImageSurfaceView extends GLImageSurfaceView {

    private MeasureHelper mMeasureHelper;
    /**
     * 设置缩放比（触摸缩放比）
     */
    private float mZoomScale = 1.0f;
    public DVImageSurfaceView(Context context) {
        super(context);
        init();
    }

    public DVImageSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    {
        mMeasureHelper = new MeasureHelper();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] measuredSize = mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        mTextureWidth = measuredSize[0];
        mTextureHeight = measuredSize[1];
        setMeasuredDimension(measuredSize[0], measuredSize[1]);
    }

    /**
     * 初始化
     */
    private void init(){
        mMeasureHelper.setScreenScale(MeasureHelper.SCREEN_SCALE_BY_SELF);
    }

    /**
     * 设置图片
     * @param bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        mMeasureHelper.setVideoRatio(bitmap.getWidth(),bitmap.getHeight());
        mMeasureHelper.setVideoSize(bitmap.getWidth(),bitmap.getHeight());
        super.setBitmap(bitmap);
    }

    /**
     * 设置缩放模式
     */
    public void setScaleType(int scaleType){
        mMeasureHelper.setScreenScale(scaleType);
        requestLayout();
    }

    /**
     * 获取当前缩放模式
     * @return
     */
    public int getScaleType(){
        return mMeasureHelper.getCurrentScreenScale();
    }

    /**
     * 计算视图大小
     */
    @Override
    protected void calculateViewSize() {
        if (mTextureWidth == 0 || mTextureHeight == 0) {
            return;
        }
        if (mViewWidth == 0 || mViewHeight == 0) {
            mViewWidth = getWidth();
            mViewHeight = getHeight();
        }
        float ratio = mTextureWidth * 1.0f / mTextureHeight;
        double viewAspectRatio = (double) mViewWidth / mViewHeight;
        if (ratio < viewAspectRatio) {
            mViewWidth = (int) (mViewHeight * ratio);
        } else {
            mViewHeight = (int) (mViewWidth / ratio);
        }
//        ViewGroup.LayoutParams layoutParams = getLayoutParams();
//        layoutParams.width = mViewWidth;
//        layoutParams.height = mViewHeight;
//        setLayoutParams(layoutParams);
    }

    /**
     * 设置缩放比（触摸缩放比）
     * @param mZoomScale
     */
    public void setZoomScale(float mZoomScale){
        this.mZoomScale = mZoomScale;
        mMeasureHelper.setZoomScale(mZoomScale);
    }
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return false;
//
//    }
}
