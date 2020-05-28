package com.devil.library.video.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.devil.library.media.R;
import com.devil.library.media.utils.FileUtils;
import com.devil.library.media.view.TipLoadDialog;
import com.devil.library.video.listener.OnVideoTrimListener;
import com.devil.library.video.utils.MeasureHelper;
import com.devil.library.video.view.DVVideoView;
import com.devil.library.video.view.VideoCropView;
import com.miyouquan.library.DVPermissionUtils;

import java.io.File;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;

/**
 * 视频裁剪界面
 */
public class DVVideoCropActivity extends AppCompatActivity implements View.OnClickListener{
    /**监听回调*/
    public static OnVideoTrimListener videoTrimListener;

    //上下文
    private Activity mActivity;
    //视频地址
    private String videoPath;
    //视频剪辑保存地址（调用者传入，可以只给地址，不需要先创建文件）
    private String savePath;
    //文件最终保存的地址
    private File saveFile;
    //宽高比中的宽
    private int widthRatio;
    //宽高比中的高
    private int heightRatio;
    //视频真实高度
    private float videoRealHeight;
    //视频真实宽度
    private float videoRealWidth;

    //裁剪框
    private VideoCropView view_crop;
    //视频播放view
    private DVVideoView mVideoView;
    //加载框
    private TipLoadDialog loadDialog;
    //确定按钮
    private Button btn_sure;
    //返回按钮
    private Button btn_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        fullScreen();

        setContentView(R.layout.activity_dv_crop_video);
        initView();
        initData();
    }

    /**
     * 全屏
     */
    private void fullScreen(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 初始化view
     */
    private void initView(){
        view_crop = findViewById(R.id.view_crop);
        view_crop.setCanEdit(false);
//        ImageUtils.setWidthHeightWithRatio(view_crop, ScreenUtils.getScreenWidth(mActivity),16,9);

        mVideoView = findViewById(R.id.mVideoView);
        mVideoView.setDragEnable(true);
        mVideoView.setZoomEnable(true);
        mVideoView.setLoop(true);
        btn_sure = findViewById(R.id.btn_sure);
        btn_back = findViewById(R.id.btn_back);

        btn_sure.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData(){
        Intent intent = getIntent();
        videoPath = intent.getStringExtra("videoPath");
        savePath = intent.getStringExtra("savePath");
        widthRatio = intent.getIntExtra("widthRatio",16);
        heightRatio = intent.getIntExtra("heightRatio",9);
        if (TextUtils.isEmpty(videoPath)){
            showMessage("请先传入视频地址，再打开裁剪界面");
            finish();
//            throw new RuntimeException("请先传入视频地址，再打开裁剪界面");
        }

        //获取视频宽高
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);
        String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        try {
            videoRealHeight = Float.parseFloat(height);
            videoRealWidth = Float.parseFloat(width);
            if (videoRealWidth > videoRealHeight){//横向视频
                //把视频展示成widthRatio:heightRatio
                mVideoView.setHorizontalScaleType(MeasureHelper.SCREEN_SCALE_BY_SELF);
                mVideoView.setVideoRatio(widthRatio,heightRatio);
            }else{//竖向视频
                //把视频展示成居中裁剪
                mVideoView.setVerticalScaleType(MeasureHelper.SCREEN_SCALE_CENTER_CROP);
            }

            mVideoView.setVideoRealSize((int)videoRealWidth,(int) videoRealHeight);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        retriever.release();

        view_crop.post(()->{
            float cropViewWidth = view_crop.getWidth();
            float cropViewHeight = view_crop.getHeight();
            float cropRectWidth = cropViewWidth;
            float cropRectHeight = cropRectWidth / widthRatio * heightRatio;
            float topMargin = cropViewHeight / 2 - cropRectHeight / 2;
            //设置裁剪框位置和大小
            view_crop.setMargin(0,topMargin,0,topMargin);
            //视频控件移动
            mVideoView.setCropToolLocation(new float[]{0,topMargin,cropRectWidth,cropRectHeight});

//            //在view.post（Runable）里获取，即等布局变化后
//            //裁剪框移动
//            int[] renderLocation = mVideoView.getRenderLocation();
//            int renderX = renderLocation[0]; // view距离 屏幕左边的距离（即x轴方向）
//            int renderY = renderLocation[1]; // view距离 屏幕顶边的距离（即y轴方向）
//            int[] renderSize = mVideoView.getRenderSize();
//            int[] mVideoViewLocation = mVideoView.getLocation();
//            int mVideoViewX = mVideoViewLocation[0]; // view距离 屏幕左边的距离（即x轴方向）
//            int mVideoViewY = mVideoViewLocation[1]; // view距离 屏幕顶边的距离（即y轴方向）
//            int[] mVideoViewSize = mVideoView.getSize();
//
//            int finalY = renderY > 0 ? renderY : mVideoViewY;
//            int finalHeight = renderSize[1] > mVideoViewSize[1] ? mVideoViewSize[1] : renderSize[1];
//            //设置裁剪框上下滑动的距离
//            view_crop.setOnlyMoveLimit(finalY,cropViewHeight - finalY - finalHeight);
        });
        mVideoView.setVideoPath(videoPath);
        mVideoView.start();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.release();
        if (videoTrimListener != null){
            videoTrimListener = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_sure){//确定
            //检查权限并开始
            checkPermissionAndStart();
        }else if(v.getId() == R.id.btn_back){//返回
            if (videoTrimListener != null){
                videoTrimListener.onVideoTrimCancel();
            }
            finish();
        }
    }

    /**
     * 检查权限并开始
     */
    private void checkPermissionAndStart(){
        //判断是否有权限操作
        String[] permissions = DVPermissionUtils.arrayConcatAll(DVPermissionUtils.PERMISSION_CAMERA,DVPermissionUtils.PERMISSION_FILE_STORAGE,DVPermissionUtils.PERMISSION_MICROPHONE);
        if (!DVPermissionUtils.verifyHasPermission(this,permissions)){
            DVPermissionUtils.requestPermissions(this, permissions, new DVPermissionUtils.OnPermissionListener() {
                @Override
                public void onPermissionGranted() {
                    //开始裁剪
                    startCrop();
                }

                @Override
                public void onPermissionDenied() {
                    showMessage(getString(R.string.permission_denied_tip));
                    finish();
                }
            });
        }else{
            //开始裁剪
            startCrop();
        }
    }

    /**
     * 开始裁剪
     */
    private void startCrop(){
        if (loadDialog == null){
            loadDialog = new TipLoadDialog(this);
            loadDialog.setCancelable(false);
            loadDialog.setCanceledOnTouchOutside(false);
        }
        loadDialog.setMsgAndType("玩命裁剪中...",TipLoadDialog.ICON_TYPE_LOADING).show();

        //开始裁剪
        float finalX = 0;
        float finalY = 0;
        float finalWidth = 0;
        float finalHeight = 0;

        //获取裁剪框位置
        float[] cutValue = view_crop.getCutValue();
        float cutX = cutValue[0];
        float cutY = cutValue[1];
        float cutWidth = cutValue[2];
        float cutHeight = cutValue[3];
        //视频显示位置
        int[] location = mVideoView.getRenderLocation();
        float videoX = location[0];
        float videoY = location[1];
//        int[] size = mVideoView.getRenderSize();
//        float videoWidth = size[0];
//        float videoHeight = size[1];
        //计算裁剪框在视频的位置
        float[] sizeRatio = mVideoView.getSizeRatio();
        float widthRatio = sizeRatio[0];
        float heightRatio = sizeRatio[1];
        finalX = (cutX - videoX) * widthRatio;
        finalY = (cutY - videoY) * heightRatio;
        finalWidth = cutWidth * widthRatio;
        finalHeight = cutHeight * heightRatio;
        if ((Math.abs(finalX) + finalWidth) > videoRealWidth){
            finalX = 0;
            finalWidth = videoRealWidth;
        }
        if ((Math.abs(finalY) + finalHeight) > videoRealHeight){
            finalY = 0;
            finalHeight = videoRealHeight;
        }

        //创建裁剪后保存的文件
        saveFile = new File(savePath);
        if (saveFile.isDirectory()){
            String finalSavePath = savePath + File.separator + System.currentTimeMillis() + File.separator + ".mp4";
            saveFile = new File(finalSavePath);
        }
        FileUtils.createFile(saveFile);
        //开始裁剪
        EpVideo epVideo = new EpVideo(videoPath);
        epVideo.crop((int)finalWidth,(int)finalHeight,(int)finalX,(int)finalY);
        EpEditor.OutputOption outputOption = new EpEditor.OutputOption(saveFile.getAbsolutePath());
        EpEditor.exec(epVideo, outputOption, new OnEditorListener() {
            @Override
            public void onSuccess() {
                //回调
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //刷新媒体库
                        try {
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri uri = Uri.fromFile(saveFile);
                            intent.setData(uri);
                            mActivity.sendBroadcast(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        loadDialog.dismiss();
                        if (videoTrimListener != null){
                            videoTrimListener.onVideoTrimSuccess(saveFile.getAbsolutePath());
                        }else{
                            showMessage("裁剪成功");
                        }
                        loadDialog.dismiss();
                        finish();
                    }
                });
            }

            @Override
            public void onFailure() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (videoTrimListener != null){
                            videoTrimListener.onVideoTrimError("裁剪失败");
                        }else{
                            showMessage("裁剪失败");
                        }
                        loadDialog.dismiss();
                    }
                });

            }

            @Override
            public void onProgress(final float progress) {
                //这里获取处理进度
//                Log.d("VideoTrim","裁剪进度-->"+progress);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (videoTrimListener != null){
                            videoTrimListener.onVideoTrimProgress(progress);
                        }
                    }
                });

            }
        });

        mVideoView.pause();
    }

    /**
     * 显示提示信息
     * @param message
     */
    private void showMessage(final String message){
        if (Looper.myLooper() == Looper.getMainLooper()){
            Toast.makeText(mActivity,""+message,Toast.LENGTH_SHORT).show();
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mActivity,""+message,Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
