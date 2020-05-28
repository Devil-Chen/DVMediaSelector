package com.devil.library.video.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.devil.library.media.R;
import com.devil.library.video.utils.ScreenUtils;

/**
 * 视频裁剪框
 */
public class VideoCropView extends View {

    private int measuredWidth;
    private int measuredHeight;
    private Paint paint;
    private int dp3;
    private int cornerLength;
    private float marginLeft;
    private float marginRight;
    private float marginTop;
    private float marginBottom;
    private int dp1;
    //绘制阴影
    private Paint rectPaint;

    public VideoCropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public VideoCropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoCropView(Context context) {
        super(context);
        init();
    }

    private void init() {

        marginLeft = 0;
        marginRight = 0;
        marginTop = 0;
        marginBottom = 0;
        dp3 = (int) getResources().getDimension(R.dimen.dp3);
        dp1 = (int) getResources().getDimension(R.dimen.dp1);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);


        rectPaint = new Paint();
        //绘制四周矩形阴影区域
        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setColor(Color.BLACK);
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setAlpha(180);//取值范围为0~255，数值越小越透明
    }

    float downX;
    float downY;
    float lastSlideX;
    float lastSlideY;

    boolean isLeft;
    boolean isRight;
    boolean isTop;
    boolean isBottom;
    boolean isMove;

    float rectLeft;
    float rectRight;
    float rectTop;
    float rectBottom;

    boolean onlyMove = true;
    float onlyMoveLimitTop = 0;
    float onlyMoveLimitBottom = 0;
    //是否可编辑大小和移动
    boolean isCanEdit = true;

    /**
     * 是否可编辑大小和移动
     * @param isCanEdit
     */
    public void setCanEdit(boolean isCanEdit){
        this.isCanEdit = isCanEdit;
    }

    /**
     * 是否只能滑动
     * @param onlyMove
     */
    public void setOnlyMove(boolean onlyMove){
        this.onlyMove = onlyMove;
    }

    /**
     * 设置只能滑动的状态上下边距限制
     */
    public void setOnlyMoveLimit(float top,float bottom){
        this.onlyMoveLimitTop = top;
        this.onlyMoveLimitBottom = bottom;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isCanEdit) return false;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:

                downX = event.getX();
                downY = event.getY();

                if (onlyMove){//是否只允许滑动
                    isMove = true;
                    return true;
                }

                //判断手指的范围在左面还是右面
                if(Math.abs(rectLeft-downX) < cornerLength){
                    isLeft = true;
                }else if(Math.abs(rectRight-downX) < cornerLength){
                    isRight = true;
                }
                //判断手指的范围在上面还是下面
                if(Math.abs(rectTop-downY) < cornerLength){
                    isTop = true;
                }else if(Math.abs(rectBottom-downY) < cornerLength){
                    isBottom = true;
                }
                //如果手指范围没有在任何边界位置, 那么我们就认为用户是想拖拽框体
                if(!isLeft && !isTop && !isRight && !isBottom){
                    isMove = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                //得到手指移动距离
                float slideX = moveX-downX+lastSlideX;
                float slideY = moveY-downY+lastSlideY;

                if(isMove){//判断是否是拖拽模式
                    rectLeft += slideX;
                    rectRight += slideX;
                    rectTop += slideY;
                    rectBottom += slideY;
                    if (onlyMove){//纯滑动状态
                        //同时改变left和right值, 达到左右移动的效果
                        if(rectLeft < 0 || rectRight > measuredWidth) {//判断x轴的移动边界
                            rectLeft -= slideX;
                            rectRight -= slideX;
                        }
                        //同时改变top和bottom值, 达到上下移动的效果
                        if(rectTop < onlyMoveLimitTop || rectBottom > measuredHeight - onlyMoveLimitBottom){//判断y轴的移动边界
                            rectTop -= slideY;
                            rectBottom -= slideY;
                        }
                    }else{
                        //同时改变left和right值, 达到左右移动的效果
                        if(rectLeft < marginLeft || rectRight > measuredWidth - marginRight) {//判断x轴的移动边界
                            rectLeft -= slideX;
                            rectRight -= slideX;
                        }
                        //同时改变top和bottom值, 达到上下移动的效果
                        if(rectTop < marginTop || rectBottom > measuredHeight - marginBottom){//判断y轴的移动边界
                            rectTop -= slideY;
                            rectBottom -= slideY;
                        }
                    }

                }else{//更改边框大小模式
                    //改变边框的宽度
                    if(isLeft){
                        rectLeft += slideX;
                        if(rectLeft < marginLeft) rectLeft = marginLeft;
                        if(rectLeft > rectRight-cornerLength*2) rectLeft = rectRight-cornerLength*2;
                    }else if(isRight){
                        rectRight += slideX;
                        if(rectRight > measuredWidth-marginRight) rectRight = measuredWidth- marginRight;
                        if(rectRight < rectLeft+cornerLength*2) rectRight = rectLeft+cornerLength*2;
                    }
                    //改变边框的高度, 如果两个都满足(比如手指在边角位置),那么就呈现一种缩放状态
                    if(isTop){
                        rectTop += slideY;
                        if(rectTop < marginTop) rectTop = marginTop;
                        if(rectTop > rectBottom-cornerLength*2) rectTop = rectBottom-cornerLength*2;
                    }else if(isBottom){
                        rectBottom += slideY;
                        if(rectBottom > measuredHeight-marginBottom) rectBottom = measuredHeight- marginBottom;
                        if(rectBottom < rectTop+cornerLength*2) rectBottom = rectTop+cornerLength*2;
                    }
                }
                //实时触发onDraw()方法
                invalidate();

                downX = moveX;
                downY = moveY;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isLeft = false;
                isRight = false;
                isTop = false;
                isBottom = false;
                isMove = false;
                break;
        }
        return true;
    }

    /**
     * 得到裁剪区域的值{x,y,width,height}
     */
    public float[] getCutValue(){
        float[] arr = new float[4];
        arr[0] = rectLeft;
        arr[1] = rectTop;
        arr[2] = rectRight;//占满横向时，rectRight即为本view宽度
        arr[3] = rectBottom - rectTop;
        return arr;
    }

    public int getRectWidth(){
        return (int) (measuredWidth-marginLeft-marginRight);
    }

    public int getRectHeight(){
        return (int) (measuredHeight-marginTop-marginBottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(measuredWidth == 0) {
            initParams();
        }
    }

    public void setMargin(float left, float top, float right, float bottom) {

        marginLeft = left;
        marginTop = top;
        marginRight = right;
        marginBottom = bottom;
        initParams();
        invalidate();
    }

    private void initParams(){

        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight();
        cornerLength = measuredWidth / 10;

        rectLeft = marginLeft;
        rectRight = measuredWidth- marginRight;
        rectTop = marginTop;
        rectBottom = measuredHeight- marginBottom;
    }



    @Override
    protected void onDraw(Canvas canvas) {

        //上阴影
        canvas.drawRect(0, 0, rectRight, rectTop, rectPaint);
        //下阴影
        canvas.drawRect(0, rectBottom, rectRight, getHeight(), rectPaint);

        paint.setStrokeWidth(dp1);
        //绘制裁剪区域的矩形, 传入margin值来确定大小
        canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);
        //绘制四条分割线和四个角
        drawLine(canvas, rectLeft, rectTop, rectRight, rectBottom);
    }

    /**
     * 绘制四条分割线和四个角
     */
    private void drawLine(Canvas canvas, float left, float top, float right, float bottom){

        paint.setStrokeWidth(1);
        //绘制四条分割线
        float startX = (right-left)/3+left;
        float startY = top;
        float stopX = (right-left)/3+left;
        float stopY = bottom;
        canvas.drawLine(startX, startY, stopX, stopY, paint);

        startX = (right-left)/3*2+left;
        startY = top;
        stopX = (right-left)/3*2+left;
        stopY = bottom;
        canvas.drawLine(startX, startY, stopX, stopY, paint);

        startX = left;
        startY = (bottom-top)/3+top;
        stopX = right;
        stopY = (bottom-top)/3+top;
        canvas.drawLine(startX, startY, stopX, stopY, paint);

        startX = left;
        startY = (bottom-top)/3*2+top;
        stopX = right;
        stopY = (bottom-top)/3*2+top;
        canvas.drawLine(startX, startY, stopX, stopY, paint);

        paint.setStrokeWidth(dp3);
        //绘制四个角
        startX = left-dp3/2;
        startY = top;
        stopX = left+cornerLength;
        stopY = top;
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        startX = left;
        startY = top;
        stopX = left;
        stopY = top+cornerLength;
        canvas.drawLine(startX, startY, stopX, stopY, paint);

        startX = right+dp3/2;
        startY = top;
        stopX = right-cornerLength;
        stopY = top;
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        startX = right;
        startY = top;
        stopX = right;
        stopY = top+cornerLength;
        canvas.drawLine(startX, startY, stopX, stopY, paint);

        startX = left;
        startY = bottom;
        stopX = left;
        stopY = bottom-cornerLength;
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        startX = left-dp3/2;
        startY = bottom;
        stopX = left+cornerLength;
        stopY = bottom;
        canvas.drawLine(startX, startY, stopX, stopY, paint);

        startX = right+dp3/2;
        startY = bottom;
        stopX = right-cornerLength;
        stopY = bottom;
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        startX = right;
        startY = bottom;
        stopX = right;
        stopY = bottom-cornerLength;
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }
}