package com.devil.test;

import android.app.Activity;
import android.content.Context;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.os.Bundle;

import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.devil.library.media.MediaSelectorManager;
import com.devil.library.media.common.ImageLoader;
import com.devil.library.media.enumtype.DVCameraType;
import com.devil.library.media.listener.OnSelectMediaListener;
import com.devil.library.media.config.DVCameraConfig;
import com.devil.library.media.config.DVListConfig;
import com.devil.library.media.enumtype.DVMediaType;
import com.devil.library.video.VideoMediaManager;
import com.devil.library.video.listener.OnVideoTrimListener;
import com.devil.player.ExoMediaPlayer;
import com.devil.player.IjkPlayer;


import java.util.List;

/**
 * 测试
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Activity mActivity;
    //结果
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        this.mActivity = this;

        tvResult = findViewById(R.id.tvResult);

        //设置加载器
        MediaSelectorManager.getInstance().initImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, final String path, ImageView imageView) {
                Glide.with(context).load(path).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG,"加载失败--》"+e.getMessage() + "\t加载地址-->"+path);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(imageView);
            }
        });

    }

    /**
     * 默认配置的多选测试
     */
    public void defaultConfigMultiSelect(View view){

        //打开界面
        MediaSelectorManager.openSelectMediaWithConfig(this, MediaSelectorManager.getDefaultListConfigBuilder().build(), new OnSelectMediaListener() {
            @Override
            public void onSelectMedia(List<String> li_path) {
                for (String path : li_path) {
                    tvResult.append(path + "\n");
                }
            }
        });
    }

    /**
     * 默认配置不要预览的多选测试
     */
    public void multiSelectNoPreview(View view){
        DVListConfig config = MediaSelectorManager.getDefaultListConfigBuilder()
                .hasPreview(false).build();
        //打开界面
        MediaSelectorManager.openSelectMediaWithConfig(this,config, new OnSelectMediaListener() {
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
        DVListConfig config = MediaSelectorManager.getDefaultListConfigBuilder()
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
                .fileCachePath(this.getCacheDir().getPath())
                //选择类型
                .mediaType(DVMediaType.ALL)
                //设置是否包含预览
                .hasPreview(true)
                .build();

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
     * 多选视频快速加载测试
     */
    public void multiSelectVideoQuickLoad(View view){

        //清除缓存，测试效果
        Glide.get(this).clearMemory();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(mActivity).clearDiskCache();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //这里才是开始调用
                        DVListConfig config = MediaSelectorManager.getDefaultListConfigBuilder()
                                .mediaType(DVMediaType.VIDEO)
                                .quickLoadVideoThumb(true)
                                .hasPreview(true).build();
                        //打开界面
                        MediaSelectorManager.openSelectMediaWithConfig(mActivity,config, new OnSelectMediaListener() {
                            @Override
                            public void onSelectMedia(List<String> li_path) {
                                for (String path : li_path) {
                                    tvResult.append(path + "\n");
                                }
                            }
                        });
                    }
                });
            }
        }).start();

    }

    /**
     * 多选视频使用加载框架加载首帧测试
     */
    public void multiSelectVideoWithLoader(View view){
        //清除缓存，测试效果
        Glide.get(this).clearMemory();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(mActivity).clearDiskCache();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //这里才是开始调用
                        DVListConfig config = MediaSelectorManager.getDefaultListConfigBuilder()
                                .mediaType(DVMediaType.VIDEO)
                                .quickLoadVideoThumb(false)
                                .hasPreview(true).build();
                        //打开界面
                        MediaSelectorManager.openSelectMediaWithConfig(mActivity,config, new OnSelectMediaListener() {
                            @Override
                            public void onSelectMedia(List<String> li_path) {
                                for (String path : li_path) {
                                    tvResult.append(path + "\n");
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 单选测试
     * @param view
     */
    public void singleSelect(View view) {
        tvResult.setText("");
        DVListConfig config = MediaSelectorManager.getDefaultListConfigBuilder()
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
                //设置全部类型
                .mediaType(DVMediaType.ALL)
//                //是否需要裁剪
//                .needCrop(true)
//                //裁剪大小
//                .cropSize(1, 1, 200, 200)
                .build();

        MediaSelectorManager.openSelectMediaWithConfig(mActivity, config, new OnSelectMediaListener() {
            @Override
            public void onSelectMedia(List<String> li_path) {
                for (String path : li_path) {
                    tvResult.append(path + "\n");
                }
            }
        });
    }

    /**
     * 图片编辑测试
     * @param view
     */
    public void editPhoto(View view) {
        tvResult.setText("");
        DVListConfig config = MediaSelectorManager.getDefaultListConfigBuilder()
                // 是否多选
                .multiSelect(false)
                .build();

        MediaSelectorManager.openSelectMediaWithConfig(mActivity, config, new OnSelectMediaListener() {
            @Override
            public void onSelectMedia(List<String> li_path) {
//                for (String path : li_path) {
//                    tvResult.append(path + "\n");
//                }
                MediaSelectorManager.openEditPhoto(mActivity, li_path.get(0), config, new OnSelectMediaListener() {
                    @Override
                    public void onSelectMedia(List<String> li_path) {
                        for (String path : li_path) {
                            tvResult.append(path + "\n");
                        }
                    }
                });
            }
        });
    }

    /**
     * 打开照相机
     * @param view
     */
    public void openCamera(View view) {
        tvResult.setText("");
        DVCameraConfig config = MediaSelectorManager.getDefaultCameraConfigBuilder()
                //相机的类型(系统照相机、普通照相机、美颜相机)默认普通照相机
                .cameraType(DVCameraType.NORMAL)
                //是否需要裁剪
                .needCrop(true)
                //裁剪大小
                .cropSize(1, 1, 200, 200)
                //媒体类型（如果是使用系统照相机，必须指定DVMediaType.PHOTO或DVMediaType.VIDEO）
                .mediaType(DVMediaType.ALL)
                //设置录制时长
                .maxDuration(10)
                //闪光灯是否启用
                .flashLightEnable(true)
                .build();

        MediaSelectorManager.openCameraWithConfig(mActivity, config, new OnSelectMediaListener() {
            @Override
            public void onSelectMedia(List<String> li_path) {
                for (String path : li_path) {
                    tvResult.append(path + "\n");
                }
            }
        });
    }

    /**
     * 打开美颜照相机
     * @param view
     */
    public void openBeautyCamera(View view) {
        tvResult.setText("");
        DVCameraConfig config = MediaSelectorManager.getDefaultCameraConfigBuilder()
                //相机的类型(系统照相机、普通照相机、美颜相机)默认普通照相机
                .cameraType(DVCameraType.BEAUTY)
                //是否需要裁剪
                .needCrop(true)
                //裁剪大小
                .cropSize(1, 1, 200, 200)
                //媒体类型（如果是使用系统照相机，必须指定DVMediaType.PHOTO或DVMediaType.VIDEO）
                .mediaType(DVMediaType.ALL)
                //设置录制时长
                .maxDuration(10)
                //闪光灯是否启用
                .flashLightEnable(true)
                .build();

        MediaSelectorManager.openCameraWithConfig(mActivity, config, new OnSelectMediaListener() {
            @Override
            public void onSelectMedia(List<String> li_path) {
                for (String path : li_path) {
                    tvResult.append(path + "\n");
                }
            }
        });
    }

    /**
     * 视频裁剪测试
     */
    public void videoTrim(View view){
        tvResult.setText("");
        DVListConfig config = MediaSelectorManager.getDefaultListConfigBuilder()
                // 是否多选
                .multiSelect(false)
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
                .mediaType(DVMediaType.VIDEO)
                .build();

        MediaSelectorManager.openSelectMediaWithConfig(mActivity, config, new OnSelectMediaListener() {
            @Override
            public void onSelectMedia(List<String> li_path) {
                String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/saveFile/" + System.currentTimeMillis() + ".mp4";

                startVideoTrim(li_path.get(0),savePath);
            }
        });
    }

    /**
     * 开始视频剪辑
     * @param videoPath
     * @param savePath
     */
    private void startVideoTrim(String videoPath,String savePath){
        VideoMediaManager.getInstance().setMediaPlayer(new ExoMediaPlayer(mActivity));
        VideoMediaManager.openVideoTrimActivity(mActivity, videoPath, savePath, new OnVideoTrimListener() {
            @Override
            public void onVideoTrimSuccess(String savePath) {
                Toast.makeText(mActivity,"剪辑成功-->"+savePath,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVideoTrimError(String msg) {
                Toast.makeText(mActivity,"剪辑失败-->"+msg,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVideoTrimCancel() {
                Toast.makeText(mActivity,"取消剪辑",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVideoTrimProgress(float progress) {

            }
        });
    }


    /**
     * 视频裁剪测试
     */
    public void videoCrop(View view){
        tvResult.setText("");
        DVListConfig config = MediaSelectorManager.getDefaultListConfigBuilder()
                // 是否多选
                .multiSelect(false)
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
                .mediaType(DVMediaType.VIDEO)
                .build();

        MediaSelectorManager.openSelectMediaWithConfig(mActivity, config, new OnSelectMediaListener() {
            @Override
            public void onSelectMedia(List<String> li_path) {
                String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/saveFile/" + System.currentTimeMillis() + ".mp4";

                VideoMediaManager.getInstance().setMediaPlayer(new ExoMediaPlayer(mActivity));
                VideoMediaManager.openVideoCropActivity(mActivity,li_path.get(0),savePath,null);
            }
        });
    }

}
