package com.devil.library.media.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.devil.library.media.R;
import com.devil.library.media.enumtype.ImageFilterType;
import com.devil.library.media.listener.OnAdjustChangeListener;


/**
 * 滤镜调节view
 */
public class FilterAdjustView extends LinearLayout{
    //内容view
    private View mContentView;
    //上下文
    private Context mContext;
    private TwoLineSeekBar mSeekBar;
    private float contrast = 100.0f;
    private float exposure = 0.0f;
    private float saturation = 100.0f;
    private float sharpness = 0.0f;
    private float brightness = 0.0f;
    private float hue = 0.0f;
    private RadioGroup mRadioGroup;
    private ImageFilterType type = ImageFilterType.NONE;
    private ImageView mLabel;
    private TextView mVal;

    //回调
    private OnAdjustChangeListener listener;

    public FilterAdjustView(Context context) {
        super(context);
        //初始化
        init();
    }

    public FilterAdjustView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //初始化
        init();
    }

    public FilterAdjustView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FilterAdjustView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        //初始化
        init();
    }

    public void setOnAdjustChangeListener(OnAdjustChangeListener listener){
        this.listener = listener;
    }

    /**
     * 初始化
     */
    private void init(){
        mContext = getContext();
        mContentView = LayoutInflater.from(getContext()).inflate(R.layout.view_dv_filter_adjust,this);
        mRadioGroup = mContentView.findViewById(R.id.rg_adjust);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (checkedId == R.id.rb_contrast){// 对比度 0.0 ~ 4.0f
                    type = ImageFilterType.CONTRAST;
                    mSeekBar.reset();
                    mSeekBar.setSeekLength(0, 400, 100, 1);
                    mSeekBar.setValue(contrast);
                    mLabel.setBackgroundResource(R.drawable.selector_dv_image_edit_adjust_contrast);
                }else if(checkedId == R.id.rb_exposure){// 曝光 -10.0f ~ 10.0f
                    type = ImageFilterType.EXPOSURE;
                    mSeekBar.reset();
                    mSeekBar.setSeekLength(-1000, 1000, 0, 1);
                    mSeekBar.setValue(exposure);
                    mLabel.setBackgroundResource(R.drawable.selector_dv_image_edit_adjust_exposure);
                }else if(checkedId == R.id.rb_saturation){// 饱和度 0 ~ 2.0f
                    type = ImageFilterType.SATURATION;
                    mSeekBar.reset();
                    mSeekBar.setSeekLength(0, 200, 100, 1);
                    mSeekBar.setValue(saturation);
                    mLabel.setBackgroundResource(R.drawable.selector_dv_image_edit_adjust_saturation);
                }else if(checkedId == R.id.rb_sharpness){// 锐度 -4.0f ~ 4.0f
                    type = ImageFilterType.SHARPEN;
                    mSeekBar.reset();
                    mSeekBar.setSeekLength(-400, 400, 0, 1);
                    mSeekBar.setValue(sharpness);
                    mLabel.setBackgroundResource(R.drawable.selector_dv_image_edit_adjust_sharpness);
                }else if(checkedId == R.id.rb_bright){// 亮度值 -1.0f ~ 1.0f
                    type = ImageFilterType.BRIGHTNESS;
                    mSeekBar.reset();
                    mSeekBar.setSeekLength(-100, 100, 0, 1);
                    mSeekBar.setValue(brightness);
                    mLabel.setBackgroundResource(R.drawable.selector_dv_image_edit_adjust_bright);
                }else if(checkedId == R.id.rb_hue){// 色调 0 ~ 360
                    type = ImageFilterType.HUE;
                    mSeekBar.reset();
                    mSeekBar.setSeekLength(0, 360, 0, 1);
                    mSeekBar.setValue(hue);
                    mLabel.setBackgroundResource(R.drawable.selector_dv_image_edit_adjust_hue);
                }

            }
        });
        mSeekBar = (TwoLineSeekBar)mContentView.findViewById(R.id.item_seek_bar);
        mSeekBar.setOnSeekChangeListener(mOnSeekChangeListener);
        mVal = (TextView)mContentView.findViewById(R.id.item_val);
        mLabel = (ImageView)mContentView.findViewById(R.id.item_label);

        //设置默认选择对比度
        setDefaultSelect();
    }

    /**
     * 设置默认选择对比度
     */
    private void setDefaultSelect(){
        type = ImageFilterType.CONTRAST;
        mSeekBar.reset();
        mSeekBar.setSeekLength(0, 400, 100, 1);
        mSeekBar.setValue(contrast);
        mLabel.setBackgroundResource(R.drawable.selector_dv_image_edit_adjust_contrast);
    }

    /**
     * 转换为滤镜参数的值
     * @param value
     * @return
     */
    private float convertToValue(float value){
        if (type == ImageFilterType.CONTRAST){//对比度
            return value / 100;
        }else if (type == ImageFilterType.EXPOSURE){//曝光
            return value / 100;
        }else if (type == ImageFilterType.BRIGHTNESS){//亮度
            return value / 100;
        }else if (type == ImageFilterType.HUE){//色调
            return value / 100;
        }else if (type == ImageFilterType.SATURATION){//饱和度
            return value / 100;
        }else if (type == ImageFilterType.SHARPEN){//锐化
            return value / 100;
        }
        return 0;
    }

    /**
     * 修改当前value
     * @param value
     */
    private void changeCurrentValue(float value){
        if (type == ImageFilterType.CONTRAST){//对比度
            contrast = value;
        }else if (type == ImageFilterType.EXPOSURE){//曝光
            exposure = value;
        }else if (type == ImageFilterType.BRIGHTNESS){//亮度
            brightness = value;
        }else if (type == ImageFilterType.HUE){//色调
            hue = value;
        }else if (type == ImageFilterType.SATURATION){//饱和度
            saturation = value;
        }else if (type == ImageFilterType.SHARPEN){//锐化
            sharpness = value;
        }
    }

    private TwoLineSeekBar.OnSeekChangeListener mOnSeekChangeListener = new TwoLineSeekBar.OnSeekChangeListener() {

        @Override
        public void onSeekStopped(float value, float step) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSeekChanged(float value, float step) {
            // TODO Auto-generated method stub
            mVal.setText(""+value);
            mLabel.setPressed(value != 0.0f);
            changeCurrentValue(value);
            if (listener != null){
                listener.onAdjustChange(type,convertToValue(value));
            }
        }
    };
}
