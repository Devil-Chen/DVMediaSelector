package com.devil.library.media.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.devil.library.media.R;

/**
 * 简易视频播放
 */
public class DVEasyVideoPlayActivity  extends AppCompatActivity implements View.OnClickListener{
    //根布局
    private View rootView;
    //视频地址
    private String videoPath;
    //返回按钮
    private RelativeLayout rl_back;
    //播放view
    private VideoView videoView;
    private MediaController mediaController;

    /**
     * 打开视频播放
     * @param mContext
     * @param path
     */
    public static void openVideo(Context mContext, String path){
        Intent intent = new Intent(mContext,DVEasyVideoPlayActivity.class);
        intent.putExtra("videoPath",path);
        mContext.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_dv_esay_video_play,null);
        setContentView(rootView);

        initView();
        initData();
    }

    /**
     * 设置全屏
     */
    private void setFullScreen(){
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        // 定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        // 获得当前窗体对象
        Window window = this.getWindow();
        // 设置当前窗体为全屏显示
        window.setFlags(flag, flag);
    }

    /**
     * 初始化控件
     */
    private void initView(){
        videoView = (VideoView)findViewById(R.id.videoView);
        rl_back = findViewById(R.id.rl_back);

        rl_back.setOnClickListener(this);

    }

    /**
     * 初始化数据
     */
    private void initData(){
        //加载指定的视频文件
        videoPath = getIntent().getStringExtra("videoPath");
        if (TextUtils.isEmpty(videoPath)){
            Toast.makeText(this,"视频地址为空无法播放",Toast.LENGTH_SHORT).show();
            finish();
        }
        videoView.setVideoPath(videoPath);

        //创建MediaController对象
        mediaController = new MediaController(this);

        //VideoView与MediaController建立关联
        videoView.setMediaController(mediaController);

        //让VideoView获取焦点
        videoView.requestFocus();

        //开始播放
        videoView.start();

        //显示进度条
        rootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaController.show();
            }
        },100);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_back){
            finish();
        }
    }
}
