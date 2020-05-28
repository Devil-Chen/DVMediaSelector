package com.devil.library.media.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.devil.library.video.utils.MeasureHelper;

/**
 * 可移动的ImageSurface（使用LinearLayout包装surfaceView）
 */
public class DVImageSurfaceLayout extends LinearLayout{
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
     * 需要显示的view
     */
    private DVImageSurfaceView surfaceView;

    //点击事件监听者
    private OnClickListener clickListener;

    //初始坐标
    private float initX;
    private float initY;

    public DVImageSurfaceLayout(Context context) {
        super(context);
        init();
    }

    public DVImageSurfaceLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DVImageSurfaceLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DVImageSurfaceLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        if (surfaceView == null){
            surfaceView = new DVImageSurfaceView(getContext());
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setGravity(Gravity.CENTER);
        this.addView(surfaceView, params);

        //跟随手指移动帮助类
        initDragHelper();
        surfaceView.post(()->{
           initX = surfaceView.getX();
           initY = surfaceView.getY();
        });
    }

    /**
     * 获取显示的view
     * @return
     */
    public DVImageSurfaceView getSurfaceView(){
        return surfaceView;
    }

    /**
     * 设置是否可以缩放
     * @param enable
     */
    public void setZoomEnable(boolean enable){
        if (enable){
            if (mScaleGesture == null){
                //初始化缩放手势监听
                initScaleGesture();
            }
        }else{
            mScaleGesture = null;
        }
    }

    /**
     * 初始化跟随手指移动帮助类
     */
    private void initDragHelper(){
        if (mViewDragHelper == null) {
            mViewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {

                private int mLeft;
                private int mTop;

                @Override
                public boolean tryCaptureView(View child, int pointerId) {
                    return child.getId() == surfaceView.getId();
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
                    return top;
                }

                @Override
                public int clampViewPositionHorizontal(View child, int left, int dx) {
                    mLeft = left;
                    return left;

                }

                @Override
                public void onViewReleased(View releasedChild, float xvel, float yvel) {
                    super.onViewReleased(releasedChild, xvel, yvel);
                    int selfX = (int) getX();
                    int selfY = (int) getY();
                    int selfWidth = getWidth();
                    int selfHeight = getHeight();
                    int surfaceX = (int) surfaceView.getX();
                    int surfaceY = (int) surfaceView.getY();
                    int surfaceWidth = surfaceView.getWidth();
                    int surfaceHeight = surfaceView.getHeight();

                    int finalLeft = mLeft;
                    int finalTop = mTop;
                    //计算顶部位置
                    if (mTop > 0) {
                        //顶部位置在view下，把顶部放回与view顶部对齐
                        finalTop = selfY;
                    } else if (mTop < 0 && (surfaceY + surfaceHeight) < (selfY + selfHeight)) {
                        //顶部位置在裁剪框下，但画面高度不足以铺满整个裁剪框，把画面底部与裁剪框底部对齐
                        finalTop = (selfY + selfHeight) - surfaceHeight;
                    }
                    //计算左边位置
                    if (mLeft > selfX) {
                        //左边位置大于裁剪框左边位置，回弹到裁剪框左边位置
                        finalLeft = selfX;
                    } else if (mLeft < selfX && (mLeft + surfaceWidth) < (selfX + selfWidth)) {
                        //左边位置小于裁剪框左边位置，但画面宽度不足以铺满整个裁剪框，回弹到裁剪框右边位置
                        finalLeft = (selfX + selfWidth) - surfaceWidth;
                    }

                    mViewDragHelper.settleCapturedViewAt(finalLeft, finalTop);
                    invalidate();
                }
            });
        }
    }

    /**
     * 初始化缩放
     */
    private void initScaleGesture(){
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
//                surfaceView.setScaleX(mZoomScale);
//                surfaceView.setScaleY(mZoomScale);

                //重绘布局
                surfaceView.setZoomScale(mZoomScale);
                surfaceView.requestLayout();
                return true;
            }
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
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

    /**
     * 一个坐标点，以某个点为缩放中心，缩放指定倍数，求这个坐标点在缩放后的新坐标值。
     * @param targetPointX 坐标点的X
     * @param targetPointY 坐标点的Y
     * @param scaleCenterX 缩放中心的X
     * @param scaleCenterY 缩放中心的Y
     * @param scale 缩放倍数
     * @return 坐标点的新坐标
     */
    private PointF scaleByPoint(float targetPointX, float targetPointY, float scaleCenterX, float scaleCenterY, float scale){
        Matrix matrix = new Matrix();
        // 将Matrix移到到当前圆所在的位置，
        // 然后再以某个点为中心进行缩放
        matrix.preTranslate(targetPointX,targetPointY);
        matrix.postScale(scale,scale,scaleCenterX,scaleCenterY);
        float[] values = new float[9];
        matrix.getValues(values);
        return new PointF(values[Matrix.MTRANS_X],values[Matrix.MTRANS_Y]);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }
    private int downX ;
    private int downY ;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = x ;
                downY = y ;
                break;
            case MotionEvent.ACTION_UP:
                int dx = x - downX;
                int dy = y - downY;
                int slop = mViewDragHelper.getTouchSlop();
                if ((Math.pow(dx, 2) + Math.pow(dy, 2)) < Math.pow(slop, 2)) {
                    //说明是点击事件
                    if (clickListener != null){
                        clickListener.onClick(this);
                    }
                }
                break;
        }//收拾的放下，抬起的处理，是为了针对点击事件做的处理
        //拖拽缩放
        if (surfaceView.getScaleType() == MeasureHelper.SCREEN_SCALE_CENTER_CROP){
            mViewDragHelper.processTouchEvent(event);
        }
        if (mScaleGesture != null){
            return mScaleGesture.onTouchEvent(event);
        }else {
            return true;
        }
    }

    @Override
    public void setOnClickListener(OnClickListener listener){
        clickListener = listener;
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper != null && mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }
}
