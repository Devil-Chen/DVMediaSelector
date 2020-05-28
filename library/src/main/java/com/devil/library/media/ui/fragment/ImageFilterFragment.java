package com.devil.library.media.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.cgfay.filter.glfilter.adjust.bean.AdjustParam;
import com.cgfay.filter.glfilter.beauty.bean.BeautyParam;
import com.cgfay.filter.glfilter.resource.FilterHelper;
import com.cgfay.filter.glfilter.resource.bean.ResourceData;
import com.cgfay.filter.widget.GLImageSurfaceView;
import com.cgfay.uitls.utils.BitmapUtils;
import com.devil.library.camera.adapter.FilterAdapter;
import com.devil.library.media.R;
import com.devil.library.media.enumtype.ImageFilterType;
import com.devil.library.media.listener.OnFilterActionListener;
import com.devil.library.media.listener.OnImageSaveListener;
import com.devil.library.media.view.DVImageSurfaceLayout;
import com.devil.library.media.view.DVImageSurfaceView;
import com.devil.library.media.view.FilterToolView;
import com.devil.library.video.utils.MeasureHelper;

import java.io.File;
import java.nio.ByteBuffer;

/**
 * 图片编辑滤镜界面
 */
public class ImageFilterFragment extends Fragment implements View.OnClickListener,OnFilterActionListener {
    //上下文
    private Context mContext;
    //滤镜
    private FilterToolView mFilterLayout;
    private ImageView image_filter;
    //美颜
    private ImageView image_beauty;
    private BeautyParam beautyParam;
    //美颜进度
    private LinearLayout line_beautySeekBar;
    private SeekBar sb_adjust;
    private SeekBar sb_complexionLevel;
    //调节参数
    private AdjustParam adjustParam;
    //显示容器
    private DVImageSurfaceView mCainImageView;
    //显示比例调整
    private ImageView image_scaleType;

    //图片地址
    private String photoPath;
    //图片保存地址
    private String saveDir;

    //保存回调
    private OnImageSaveListener saveListener;

    /**
     * 获取实例
     * @return
     */
    public static ImageFilterFragment instance(String photoPath,String newPhotoSaveDir) {
        ImageFilterFragment fragment = new ImageFilterFragment();
        Bundle bundle = new Bundle();
        bundle.putString("photoPath",photoPath);
        bundle.putString("saveDir",newPhotoSaveDir);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dv_image_filter, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        photoPath = getArguments().getString("photoPath");
        saveDir = getArguments().getString("saveDir");
        initFilterView(view);
    }

    /**
     * 图片保存回调
     * @param listener
     */
    public void setOnImageSaveListener(OnImageSaveListener listener){
        this.saveListener = listener;
    }

    /**
     * 初始化滤镜相关view
     */
    private void initFilterView(View mContentView){
        mFilterLayout = mContentView.findViewById(R.id.layout_filter);
        mFilterLayout.setOnFilterActionListener(this);

        image_filter = mContentView.findViewById(R.id.image_filter);
        image_filter.setOnClickListener(this);

        //美颜参数设置
        line_beautySeekBar = mContentView.findViewById(R.id.line_beautySeekBar);
        sb_adjust = mContentView.findViewById(R.id.sb_adjust);
        sb_complexionLevel = mContentView.findViewById(R.id.sb_complexionLevel);
        sb_adjust.setOnSeekBarChangeListener(beautyParamsChangeListener);
        sb_complexionLevel.setOnSeekBarChangeListener(beautyParamsChangeListener);

        //美颜图标
        image_beauty = mContentView.findViewById(R.id.image_beauty);
        image_beauty.setOnClickListener(this);

        // 图片内容布局
        DVImageSurfaceLayout imageSurfaceLayout = mContentView.findViewById(R.id.glImageLayout);
        mCainImageView = imageSurfaceLayout.getSurfaceView();
        mCainImageView.setCaptureCallback(mCaptureCallback);
        imageSurfaceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBeautySeekBar();
                hideFilterLayout();
            }
        });

        //显示比例调整
        image_scaleType = mContentView.findViewById(R.id.image_scaleType);
        image_scaleType.setOnClickListener(this);

        if (!TextUtils.isEmpty(photoPath)) {
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            mCainImageView.setBitmap(bitmap);
        }
    }


    //美颜参数监听
    private SeekBar.OnSeekBarChangeListener  beautyParamsChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            float realProgress = progress / 100f;
            if (seekBar.getId() == R.id.sb_adjust){//磨皮
                beautyParam.beautyIntensity = realProgress;
            }else if(seekBar.getId() == R.id.sb_complexionLevel){//美肤
                beautyParam.complexionIntensity = realProgress;
            }
            if (sb_adjust.getProgress() == 0 && sb_complexionLevel.getProgress() == 0){
                image_beauty.setImageResource(R.mipmap.editor_beauty_normal);
            }else{
                image_beauty.setImageResource(R.mipmap.editor_beauty_pressed);
            }
            if (mCainImageView != null) {
                mCainImageView.setBeautyFilter(beautyParam);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };


    /**
     * 截屏回调
     */
    private GLImageSurfaceView.CaptureCallback mCaptureCallback = new GLImageSurfaceView.CaptureCallback() {
        @Override
        public void onCapture(final ByteBuffer buffer, final int width, final int height) {
            String filePath = saveDir + File.separator + "DVPhoto_" + System.currentTimeMillis() + ".jpg";
            BitmapUtils.saveBitmap(filePath, buffer, width, height);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (saveListener != null){
                        saveListener.onSaveSuccess(filePath);
                    }
                }
            });
        }
    };


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.image_beauty){//美颜
            if (beautyParam == null){
                beautyParam = new BeautyParam();
            }
            //显示或隐藏美颜参数选择
            if (line_beautySeekBar.getVisibility() == View.VISIBLE){
                line_beautySeekBar.setVisibility(View.GONE);
            }else{
                line_beautySeekBar.setVisibility(View.VISIBLE);
            }
            hideFilterLayout();
        }else if(v.getId() == R.id.image_filter){//开启选择滤镜
            if (mFilterLayout.getVisibility() == View.GONE){
                mFilterLayout.setVisibility(View.VISIBLE);
            }
            hideBeautySeekBar();
        }else if(v.getId() == R.id.image_closeFilter){//关闭选择滤镜
            if (mFilterLayout.getVisibility() == View.VISIBLE){
                mFilterLayout.setVisibility(View.GONE);
            }
            hideBeautySeekBar();
        }else if(v.getId() == R.id.image_scaleType){//缩放比例调整
            if (mCainImageView.getScaleType() == MeasureHelper.SCREEN_SCALE_CENTER_CROP){
                mCainImageView.setScaleType(MeasureHelper.SCREEN_SCALE_BY_SELF);
            }else{
                mCainImageView.setScaleType(MeasureHelper.SCREEN_SCALE_CENTER_CROP);
            }
        }
    }

    /**
     * 隐藏美颜选择
     */
    private void hideBeautySeekBar(){
        if (line_beautySeekBar.getVisibility() == View.VISIBLE){
            line_beautySeekBar.setVisibility(View.GONE);
        }
    }

    /**
     * 隐藏美颜选择
     */
    private void hideFilterLayout(){
        if (mFilterLayout.getVisibility() == View.VISIBLE){
            mFilterLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 保存图片
     */
    public void startSaveImage(){
        if (mCainImageView != null) {
            mCainImageView.getCaptureFrame();
        }
    }

    @Override
    public void onCloseFilter() {
        if (mFilterLayout.getVisibility() == View.VISIBLE){
            mFilterLayout.setVisibility(View.GONE);
        }
    }

    @Override
    //颜色滤镜转换
    public void onColorFilterChanged(ResourceData resourceData) {
        if (!resourceData.name.equals("none")) {
            if (mCainImageView != null) {
                mCainImageView.setFilter(resourceData);
            }
        } else {

        }
    }

    @Override
    public void onAdjustChange(ImageFilterType type, float value) {

        if (adjustParam == null){
            adjustParam = new AdjustParam();
        }

        if (type == ImageFilterType.CONTRAST){//对比度
            adjustParam.contrast = value;
        }else if (type == ImageFilterType.EXPOSURE){//曝光
            adjustParam.exposure = value;
        }else if (type == ImageFilterType.BRIGHTNESS){//亮度
            adjustParam.brightness = value;
        }else if (type == ImageFilterType.HUE){//色调
            adjustParam.hue = value;
        }else if (type == ImageFilterType.SATURATION){//饱和度
            adjustParam.saturation = value;
        }else if (type == ImageFilterType.SHARPEN){//锐化
            adjustParam.sharpness = value;
        }
        if (mCainImageView != null) {
            mCainImageView.setAdjustFilter(adjustParam);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
