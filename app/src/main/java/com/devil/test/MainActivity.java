package com.devil.test;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.devil.library.media.MediaSelectorManager;
import com.devil.library.media.common.ImageLoader;
import com.devil.library.media.listener.OnSelectMediaListener;
import com.devil.library.media.config.DVCameraConfig;
import com.devil.library.media.config.DVListConfig;
import com.devil.library.media.enumtype.DVMediaType;


import java.util.List;

/**
 * 测试
 */
public class MainActivity extends AppCompatActivity {
    //结果
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);

        //设置加载器
        MediaSelectorManager.getInstance().initImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });
    }

    /**
     * 默认配置的多选测试
     */
    public void defaultConfigMultiSelect(View view){
        //打开界面
        MediaSelectorManager.openSelectMediaWithConfig(this, MediaSelectorManager.getDefaultListConfig(), new OnSelectMediaListener() {
            @Override
            public void onSelectMedia(List<String> li_path) {
                for (String path : li_path) {
                    tvResult.append(path + "\n");
                }
            }
        });
    }

    /**
     * 多选测试
     * @param view
     */
    public void multiSelect(View view) {
        tvResult.setText("");
        DVListConfig config = MediaSelectorManager.getDefaultListConfig()
                //是否多选
                .multiSelect(true)
                //最大选择数量
                .maxNum(9)
                //最小选择数量
                .minNum(2)
                //设置选中图标
                .checkIconResource(R.mipmap.icon_dv_checked)
                //设置非选中图标
                .unCheckIconResource(R.mipmap.icon_dv_unchecked)
                // 使用沉浸式状态栏
                .statusBarColor(Color.BLUE)
                //每行显示的数量
                .listSpanCount(3)
                //状态栏的mode
                .statusBarLightMode(true)
//                .statusBarDrakMode(true)
                //设置选择资源的类型
                .mediaType(DVMediaType.ALL)
                //设置返回图标
//                .backResourceId(R.mipmap.icon_back)
                //设置右边标题
                .rigntTitleText("所有图片")
                //设置右边标题文字颜色
                .rightTitleTextColor(Color.WHITE)
                //是否显示右边标题
                .rightTitleVisibility(View.VISIBLE)
                //设置标题文字
                .title("资源选择")
                //设置标题文字颜色
                .titleTextColor(Color.WHITE)
                //设置标题背景颜色
                .titleBgColor(Color.BLUE)
                //确定按钮文字
                .sureBtnText("确定")
                //确定按钮文字颜色
                .sureBtnTextColor(Color.WHITE)
                //确定按钮背景色（与Resource只能选择一种）
//                .sureBtnBgColor(Color.BLUE)
                //确定按钮所在布局背景色（与color只能选择一种）
                .sureBtnBgResource(R.drawable.shape_btn_default)
                //设置文件临时缓存路径
                .fileCachePath(this.getCacheDir().getPath());

        //打开界面
        MediaSelectorManager.openSelectMediaWithConfig(this, config, new OnSelectMediaListener() {
            @Override
            public void onSelectMedia(List<String> li_path) {
                for (String path : li_path) {
                    tvResult.append(path + "\n");
                }
            }
        });
    }

    /**
     * 单选测试
     * @param view
     */
    public void singleSelect(View view) {
        tvResult.setText("");
        DVListConfig config = DVListConfig.createInstance()
                // 是否多选
                .multiSelect(false)
                //第一个菜单是否显示照相机
                .needCamera(true)
                //第一个菜单显示照相机的图标
                .cameraIconResource(R.mipmap.ic_launcher)
                //每行显示的数量
                .listSpanCount(4)
                // 确定按钮文字颜色
                .sureBtnTextColor(Color.WHITE)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#3F51B5"))
                // 返回图标ResId
                .backResourceId(R.mipmap.icon_back2)
                //标题背景
                .titleBgColor(Color.parseColor("#3F51B5"))
                //是否需要裁剪
                .needCrop(true)
                //裁剪大小
                .cropSize(1, 1, 200, 200);

        MediaSelectorManager.openSelectMediaWithConfig(this, config, new OnSelectMediaListener() {
            @Override
            public void onSelectMedia(List<String> li_path) {
                for (String path : li_path) {
                    tvResult.append(path + "\n");
                }
            }
        });
    }

    /**
     * 打开照相机
     * @param view
     */
    public void openCamera(View view) {
        tvResult.setText("");
        DVCameraConfig config = DVCameraConfig.createInstance()
                //是否使用系统照相机（默认使用仿微信照相机）
                .isUseSystemCamera(false)
                //是否需要裁剪
                .needCrop(true)
                //裁剪大小
                .cropSize(1, 1, 200, 200)
                //媒体类型
                .mediaType(DVMediaType.ALL);

        MediaSelectorManager.openCameraWithConfig(this, config, new OnSelectMediaListener() {
            @Override
            public void onSelectMedia(List<String> li_path) {
                for (String path : li_path) {
                    tvResult.append(path + "\n");
                }
            }
        });
    }

}
