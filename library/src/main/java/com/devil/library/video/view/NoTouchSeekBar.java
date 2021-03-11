package com.devil.library.video.view;

import android.content.Context;
import androidx.appcompat.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 不能拖动的seekBar
 */
public class NoTouchSeekBar extends AppCompatSeekBar {
    public NoTouchSeekBar(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    public NoTouchSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }
    public NoTouchSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    /**
     * onTouchEvent 是在 SeekBar 继承的抽象类 AbsSeekBar 里
     * 你可以看下他们的继承关系
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        //原来是要将TouchEvent传递下去的,我们不让它传递下去就行了
        //return super.onTouchEvent(event);

        return false ;
    }

}
