package com.devil.library.video.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.devil.library.media.R;


import com.devil.library.media.utils.FileUtils;
import com.devil.library.video.listener.OnVideoTrimListener;
import com.devil.library.video.listener.OnSelectVideoTrimListener;
import com.devil.library.media.view.TipLoadDialog;
import com.devil.library.video.view.VideoTrimView;
import com.miyouquan.library.DVPermissionUtils;

import java.io.File;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;

/**
 * 视频剪辑界面
 */
public class DVVideoTrimActivity extends AppCompatActivity implements OnSelectVideoTrimListener {
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
    //剪辑view
    private VideoTrimView trimmerView;
    //加载框
    private TipLoadDialog loadDialog;
    //输出视频帧率,默认根据原视频
    private int frameRate;
    //输出视频码率,默认根据原视频
    private int bitRate;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mActivity = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dv_video_trim);
        //初始化
        initViewAndData();
    }

    /**
     * 初始化
     */
    private void initViewAndData(){
        Intent intent = getIntent();
        videoPath = intent.getStringExtra("videoPath");
        savePath = intent.getStringExtra("savePath");
        if (TextUtils.isEmpty(videoPath)){
            throw new RuntimeException("请先传入视频地址，再打开剪辑界面");
        }
        frameRate = intent.getIntExtra("frameRate",-1);
        bitRate = intent.getIntExtra("bitRate",-1);
        String backTitle = intent.getStringExtra("backTitle");
        String sureTitle = intent.getStringExtra("sureTitle");
        String tipText = intent.getStringExtra("tipText");

        trimmerView = findViewById(R.id.trimmerView);

        trimmerView.setOnTrimVideoListener(this);
        trimmerView.initVideoByPath(videoPath);

        //设置返回标题
        if (!TextUtils.isEmpty(backTitle)){
            trimmerView.setBackTitle(backTitle);
        }
        //设置确定标题
        if(!TextUtils.isEmpty(sureTitle)){
            trimmerView.setSureTitle(sureTitle);
        }
        //设置进度上方提示文字
        if(!TextUtils.isEmpty(tipText)){
            trimmerView.setTipText(tipText);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }
    @Override
    public void finish() {
        super.finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        trimmerView.release();
        if (videoTrimListener != null){
            videoTrimListener = null;
        }
        //删除缩略图文件夹和文件夹里面的文件
        String saveDir = mActivity.getExternalCacheDir().getAbsolutePath() + "/VideoThumb/";
        File saveFileDir = new File(saveDir);
        if (saveFileDir.exists()){
            FileUtils.deleteDirAllFile(saveFileDir);
        }
    }


    @Override
    public void onCancelSelect() {
        if (videoTrimListener != null){
            videoTrimListener.onVideoTrimCancel();
        }
        finish();
    }

    @Override
    public void onAlreadySelect(final String videoPath,final long startTime,final long endTime,final long mDuration) {
        Log.d("VideoTrim","startTime："+ startTime + "\tendTime："+endTime);
        if (TextUtils.isEmpty(savePath)){
            if (videoTrimListener != null){
                videoTrimListener.onVideoTrimError("没有设置保存地址");
            }
            return;
        }
        if (startTime == 0 && endTime == mDuration){
            //没选择裁剪时长
        }else{
            //检查权限并开始
            checkPermissionAndStart(startTime,endTime);
        }

    }

    /**
     * 检查权限并开始
     */
    private void checkPermissionAndStart(final long startTime,final long endTime){
        //判断是否有权限操作
        String[] permissions = DVPermissionUtils.arrayConcatAll(DVPermissionUtils.PERMISSION_CAMERA,DVPermissionUtils.PERMISSION_FILE_STORAGE,DVPermissionUtils.PERMISSION_MICROPHONE);
        if (!DVPermissionUtils.verifyHasPermission(this,permissions)){
            DVPermissionUtils.requestPermissions(this, permissions, new DVPermissionUtils.OnPermissionListener() {
                @Override
                public void onPermissionGranted() {
                    //开始剪辑
                    startClip(startTime,endTime);
                }

                @Override
                public void onPermissionDenied() {
                    showMessage(getString(R.string.permission_denied_tip));
                    finish();
                }
            });
        }else{
            //开始剪辑
            startClip(startTime,endTime);
        }
    }

    /**
     * 开始裁剪
     */
    private void startClip(final long startTime,final long endTime){
        if (loadDialog == null){
            loadDialog = new TipLoadDialog(this);
            loadDialog.setCancelable(false);
            loadDialog.setCanceledOnTouchOutside(false);
        }
        loadDialog.setMsgAndType("玩命剪辑中...",TipLoadDialog.ICON_TYPE_LOADING).show();

        //创建裁剪后保存的文件
        saveFile = new File(savePath);
        if (saveFile.isDirectory()){
            String finalSavePath = savePath + File.separator + System.currentTimeMillis() + File.separator + ".mp4";
            saveFile = new File(finalSavePath);
        }
        FileUtils.createFile(saveFile);
        //开始裁剪
        EpVideo epVideo = new EpVideo(videoPath);
        int startSecond = (int) (startTime / 1000);
        int endSecond = (int) (endTime / 1000);
        epVideo.clip(startSecond,endSecond - startSecond);

        EpEditor.OutputOption outputOption = new EpEditor.OutputOption(saveFile.getAbsolutePath());
        if (frameRate != -1){
            outputOption.frameRate = frameRate;//输出视频帧率
        }
        if (bitRate != -1){
            outputOption.bitRate = bitRate;//输出视频码率
        }
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
                Log.d("VideoTrim","裁剪进度-->"+progress);
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
