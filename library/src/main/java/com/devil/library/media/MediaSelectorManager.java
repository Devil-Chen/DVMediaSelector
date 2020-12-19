package com.devil.library.media;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.devil.library.media.common.ImageLoader;
import com.devil.library.media.listener.OnSelectMediaListener;
import com.devil.library.media.common.MediaTempListener;
import com.devil.library.media.config.DVCameraConfig;
import com.devil.library.media.config.DVListConfig;
import com.devil.library.media.enumtype.DVMediaType;
import com.devil.library.media.ui.activity.DVBeautyCameraActivity;
import com.devil.library.media.ui.activity.DVCameraActivity;
import com.devil.library.media.ui.activity.DVEditPhotoActivity;
import com.devil.library.media.ui.activity.DVMediaSelectActivity;
import com.devil.library.media.ui.activity.DVSystemCameraActivity;


/**
 * 选择管理者（使用了第三方库PhotoView：com.github.chrisbanes:PhotoView:2.0.0）
 */
public class MediaSelectorManager {
    //单例
    private static MediaSelectorManager instance;


    //------------------------ 实例相关变量 ---------------------------
    //图片加载器
    private ImageLoader imageLoader;
    //当前列表配置
    private DVListConfig currentListConfig;
    //当前相机配置
    private DVCameraConfig currentCameraConfig;




    /**
     * 获取单例
     * @return
     */
    public static MediaSelectorManager getInstance() {
        if (instance == null) {
            synchronized (MediaSelectorManager.class) {
                if (instance == null) {
                    instance = new MediaSelectorManager();
                }
            }
        }
        return instance;
    }



    /**
     * 图片加载必须先初始化
     *
     * @param loader
     */
    public void initImageLoader(@NonNull ImageLoader loader) {
        this.imageLoader = loader;
    }

    /**
     * 获取图片加载器
     * @return
     */
    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    /**
     * 显示图片
     * @param context
     * @param path
     * @param imageView
     */
    public void displayImage(Context context, String path, ImageView imageView) {
        if (imageLoader != null) {
            imageLoader.displayImage(context, path, imageView);
        }
    }


    /**
     * 获取当前列表选择配置
     * @return
     */
    public DVListConfig getCurrentListConfig() {
        if (currentListConfig == null){
            currentListConfig = getDefaultListConfigBuilder().build();
        }
        return currentListConfig;
    }

    /**
     * 获取当前相机选择配置
     * @return
     */
    public DVCameraConfig getCurrentCameraConfig(){
        if (currentCameraConfig == null){
            currentCameraConfig = getDefaultCameraConfigBuilder().build();
        }
        return currentCameraConfig;
    }


    /**
     * 清除当前配置
     */
    public void clean(){
        currentListConfig = null;
        currentCameraConfig = null;
        MediaTempListener.release();
    }

    /**
     * 获取默认的列表选择配置
     * @return
     */
    public static DVListConfig.Builder getDefaultListConfigBuilder(){
       return   new DVListConfig.Builder()
                // 是否多选, 默认true
                .multiSelect(true)
                // “确定”按钮背景色
                .sureBtnBgColor(Color.TRANSPARENT)
                // “确定”按钮文字颜色
                .sureBtnTextColor(Color.WHITE)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#3F51B5"))
                // 返回图标ResId
                .backResourceId(com.devil.library.media.R.mipmap.icon_dv_arrow_left_white_back)
                // 标题
                .title("选择")
                // 标题文字颜色
                .titleTextColor(Color.WHITE)
                // TitleBar背景色
                .titleBgColor(Color.parseColor("#3F51B5"));
    }

    /**
     * 获取默认的相机选择配置
     * @return
     */
    public static DVCameraConfig.Builder getDefaultCameraConfigBuilder(){
        return new DVCameraConfig.Builder();
    }

    /**
     * 启动activity
     * @param mActivity
     * @param intent
     */
    private static void startActivityRightToLeft(Activity mActivity,Intent intent){
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.anim_dv_enter_from_right,R.anim.anim_dv_out_to_left);
    }

    /**
     * 启动activity
     * @param mActivity
     * @param intent
     */
    private static void startActivityBottomToTop(Activity mActivity,Intent intent){
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.anim_dv_enter_from_bottom,R.anim.anim_dv_out_to_top);
    }

    /**
     * 开启选择资源的界面
     * @param mActivity
     */
    public static void openSelectMediaWithConfig(Activity mActivity, DVListConfig config, OnSelectMediaListener listener){
        Intent intent = new Intent(mActivity, DVMediaSelectActivity.class);

        MediaSelectorManager.getInstance().currentListConfig = config;
        MediaTempListener.setOnSelectMediaListener(listener);
        intent.putExtra("action","mediaList");

        startActivityRightToLeft(mActivity,intent);
    }

    /**
     * 开启选择资源的界面
     * @param mActivity
     */
    public static void openSelectMediaWithMediaType(Activity mActivity, DVMediaType mediaType, OnSelectMediaListener listener){
        Intent intent = new Intent(mActivity, DVMediaSelectActivity.class);

        MediaSelectorManager.getInstance().currentListConfig = getDefaultListConfigBuilder().build();
        MediaSelectorManager.getInstance().currentListConfig.mediaType = mediaType;
        MediaTempListener.setOnSelectMediaListener(listener);
        intent.putExtra("action","mediaList");

        startActivityRightToLeft(mActivity,intent);
    }

    /**
     * 开启相机的界面
     * @param mActivity
     */
    public static void openCameraWithConfig(Activity mActivity, DVCameraConfig config, OnSelectMediaListener listener){
        Intent intent = new Intent();
        switch (config.cameraType){
            case SYSTEM:
                intent.setClass(mActivity,DVSystemCameraActivity.class);
                break;
            case NORMAL:
                intent.setClass(mActivity,DVCameraActivity.class);
                break;
            case BEAUTY:
                intent.setClass(mActivity,DVBeautyCameraActivity.class);
                break;
                default:
                    break;
        }
        MediaSelectorManager.getInstance().currentCameraConfig = config;
        MediaTempListener.setOnSelectMediaListener(listener);
        intent.putExtra("action","camera");

        startActivityBottomToTop(mActivity,intent);
    }

    /**
     * 开启相机的界面（普通照相机）
     * @param mActivity
     */
    public static void openCameraWithMediaType(Activity mActivity,DVMediaType mediaType, OnSelectMediaListener listener){
        Intent intent = new Intent(mActivity, DVCameraActivity.class);

        MediaSelectorManager.getInstance().currentCameraConfig = getDefaultCameraConfigBuilder().build();
        MediaSelectorManager.getInstance().currentCameraConfig.mediaType = mediaType;

        MediaTempListener.setOnSelectMediaListener(listener);
        intent.putExtra("action","camera");

        startActivityBottomToTop(mActivity,intent);
    }

    /**
     * 打开图片编辑
     * @param mActivity
     */
    public static void openEditPhoto(Activity mActivity,String photoPath, DVListConfig config, OnSelectMediaListener listener){
        MediaTempListener.setOnSelectMediaListener(listener);
        MediaSelectorManager.getInstance().currentListConfig = config;
        Intent intent = new Intent(mActivity, DVEditPhotoActivity.class);
        intent.putExtra("photoPath",photoPath);
        mActivity.startActivity(intent);
    }

}
