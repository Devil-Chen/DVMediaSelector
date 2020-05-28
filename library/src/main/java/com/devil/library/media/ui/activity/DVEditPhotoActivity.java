package com.devil.library.media.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devil.library.camera.util.FileUtil;
import com.devil.library.media.MediaSelectorManager;
import com.devil.library.media.R;
import com.devil.library.media.common.MediaTempListener;
import com.devil.library.media.config.DVListConfig;
import com.devil.library.media.listener.OnImageSaveListener;
import com.devil.library.media.ui.fragment.ImageFilterFragment;
import com.devil.library.media.utils.FileUtils;
import com.devil.library.media.utils.StatusBarUtil;

import java.util.ArrayList;

/**
 * 图片编辑界面
 */
public class DVEditPhotoActivity extends DVBaseActivity implements View.OnClickListener{
    private Activity mContext;
    //标题栏
    private RelativeLayout rl_titleBar;
    //返回按钮
    private ImageView iv_back;
    //标题
    private TextView tv_title;
    //标题右边按钮
    private Button btn_save;
    //配置
    private DVListConfig config;
    //滤镜fragment
    private ImageFilterFragment filterFragment;
    //图片地址
    private String photoPath;
    //图片保存位置
    private String fileCachePath;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_dv_photo_edit);
        photoPath = getIntent().getStringExtra("photoPath");
        initView();
        initListener();
        setUpConfig();
        initFragment();
    }

    /**
     * 初始化view
     */
    private void initView(){
        rl_titleBar = findViewById(R.id.rl_titleBar);
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        btn_save = findViewById(R.id.btn_save);
    }

    /**
     * 初始化监听者
     */
    private void initListener(){
        iv_back.setOnClickListener(this);
        btn_save.setOnClickListener(this);
    }

    /**
     * 初始化
     */
    private void initFragment(){
        filterFragment = ImageFilterFragment.instance(photoPath,fileCachePath);
        filterFragment.setOnImageSaveListener(new OnImageSaveListener() {
            @Override
            public void onSaveSuccess(String filePath) {
                finishSelect(filePath);
            }
        });
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_content, filterFragment, ImageFilterFragment.class.getName())
                .commit();
    }

    /**
     * 根据配置设置属性
     */
    private void setUpConfig(){
        // -----------------------  根据配置设置 -----------------------
        config = MediaSelectorManager.getInstance().getCurrentListConfig();
        if (config.statusBarColor != 0){//状态栏颜色
            StatusBarUtil.setColor(mContext,config.statusBarColor);
        }
        //状态栏模式
        if (config.statusBarLightMode){
            StatusBarUtil.setLightMode(mContext);
        }else if(config.statusBarDrakMode){
            StatusBarUtil.setDarkMode(mContext);
        }
        //标题
        if (!TextUtils.isEmpty(config.title)){
            tv_title.setText(config.title);
        }
        //标题字体颜色
        if (config.titleTextColor != 0){
            tv_title.setTextColor(config.titleTextColor);
        }
        //标题栏颜色(导航栏)
        if (config.titleBgColor != 0){
            rl_titleBar.setBackgroundColor(config.titleBgColor);
        }
        //返回按钮图标
        if (config.backResourceId != 0){
            iv_back.setImageResource(config.backResourceId);
        }

        //设置文件缓存路径
        if (TextUtils.isEmpty(config.fileCachePath)){
            fileCachePath = FileUtils.createRootPath(this);
        }else{
            fileCachePath = config.fileCachePath;
        }
        //右边标题内容
        if (!TextUtils.isEmpty(config.rigntTitleText)){
            btn_save.setText(config.rigntTitleText);
        }
        //右边标题字体颜色
        if (config.rightTitleTextColor != 0){
            btn_save.setTextColor(config.rightTitleTextColor);
        }
        //是否显示右边标题
        if (config.rightTitleVisibility == View.GONE){
            btn_save.setVisibility(View.GONE);

        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back){//返回
            onBackPressed();
        }else if(v.getId() == R.id.btn_save){//保存
            filterFragment.startSaveImage();
        }
    }
    /**
     * 完成选择
     * @param filePath
     */
    private void finishSelect(String filePath) {
        Intent intent = new Intent();
        if (!TextUtils.isEmpty(filePath)) {
            intent.putExtra("result", filePath);
        }
        setResult(RESULT_OK, intent);

        if (MediaTempListener.listener != null){
            ArrayList<String> li_path = new ArrayList<>();
            li_path.add(filePath);
            MediaTempListener.listener.onSelectMedia(li_path);
        }
        onBackPressed();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaSelectorManager.getInstance().clean();
        MediaTempListener.release();
    }
}
