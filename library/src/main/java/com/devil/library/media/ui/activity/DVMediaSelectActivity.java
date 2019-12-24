package com.devil.library.media.ui.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devil.library.media.MediaSelectorManager;
import com.devil.library.media.R;
import com.devil.library.media.bean.FolderInfo;
import com.devil.library.media.bean.MediaInfo;
import com.devil.library.media.config.DVCameraConfig;
import com.devil.library.media.listener.OnItemClickListener;
import com.devil.library.media.common.MediaTempListener;
import com.devil.library.media.config.DVListConfig;
import com.devil.library.media.enumtype.DVMediaType;
import com.devil.library.media.ui.fragment.MediaListFragment;
import com.devil.library.media.ui.fragment.WatchMediaFragment;
import com.devil.library.media.utils.FileUtils;
import com.devil.library.media.utils.MediaFileTypeUtils;
import com.devil.library.media.utils.StatusBarUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * 媒体选择界面
 */
public class DVMediaSelectActivity extends AppCompatActivity implements View.OnClickListener,OnItemClickListener {
    //当前选择的MediaInfo
    public static HashMap<String,MediaInfo> map_cacheSelectInfo;

    //请求码
    private static final int IMAGE_CROP_CODE = 1;
    //裁剪图片临时创建的文件
    private File cropImageFile;
    //文件临时保存路径
    private String fileCachePath;

    //上下文
    private Activity mContext;

    //fragment
    private MediaListFragment mediaFragment;
//    private WatchMediaFragment watchMediaFragment;
    //配置
    private DVListConfig config;
    //选择文件夹按钮
    private Button btn_selectFolder;
    //完成按钮
    private Button btn_sure;
    //标题
    private TextView tv_title;
    //返回图标
    private ImageView iv_back;
    //标题栏整个布局
    private RelativeLayout rl_titleBar;
    //底部布局
    private RelativeLayout rl_bottom;

    //是否需要在finish界面的时候，清除数据
    private boolean needCleanWithFinish = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mContext = this;
        if (map_cacheSelectInfo == null){
            map_cacheSelectInfo = new HashMap<>();
        }

        setContentView(R.layout.activity_dv_media_select);
        //初始化view
        initView();
        //根据配置设置属性
        setUpConfig();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_mediaList, mediaFragment, MediaListFragment.class.getName())
                .commit();
    }

    /**
     * 初始化view
     */
    private void initView(){
        mediaFragment = MediaListFragment.instance();
//        watchMediaFragment = WatchMediaFragment.instance();

        //找到控件
        btn_selectFolder = findViewById(R.id.btn_selectFolder);
        btn_sure = findViewById(R.id.btn_sure);
        tv_title = findViewById(R.id.tv_title);
        iv_back = findViewById(R.id.iv_back);
        rl_titleBar = findViewById(R.id.rl_titleBar);
        rl_bottom = findViewById(R.id.rl_bottom);

        //设置监听
        mediaFragment.setOnItemClickListener(this);
        btn_selectFolder.setOnClickListener(this);
        btn_sure.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        //设置fragment返回栈改变监听
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0){
                    mediaFragment.refreshData();
                }
            }
        });
    }

    /**
     * 吐司显示信息
     * @param message
     */
    public void showMessage(final String message) {
        if (!TextUtils.isEmpty(message)){
            if (Looper.getMainLooper() == Looper.myLooper()){
                Toast.makeText(mContext,message+"",Toast.LENGTH_SHORT).show();
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext,message+"",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
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
        //如果是单选，隐藏底部布局
        if (!config.multiSelect){
            rl_bottom.setVisibility(View.GONE);
        }else {
            //底部布局背景色
            if (config.sureBtnLayoutBgColor != 0){
                rl_bottom.setBackgroundColor(config.sureBtnLayoutBgColor);
            }else if (config.sureBtnLayoutBgResource != 0){//确定按钮背景Resource
                rl_bottom.setBackgroundResource(config.sureBtnLayoutBgResource);
            }
        }
        //设置文件缓存路径
        if (TextUtils.isEmpty(config.fileCachePath)){
            fileCachePath = FileUtils.createRootPath(this);
        }else{
            fileCachePath = config.fileCachePath;
        }
        //右边标题内容
        if (!TextUtils.isEmpty(config.rigntTitleText)){
            btn_selectFolder.setText(config.rigntTitleText);
        }
        //右边标题字体颜色
        if (config.rightTitleTextColor != 0){
            btn_selectFolder.setTextColor(config.rightTitleTextColor);
        }
        //是否显示右边标题
        if (config.rightTitleVisibility == View.GONE){
            btn_selectFolder.setVisibility(View.GONE);

        }
        //确定按钮文字
        if (!TextUtils.isEmpty(config.sureBtnText)){
            btn_sure.setText(config.sureBtnText + "(0/" + config.maxNum + ")");
        }else{
            btn_sure.setText(btn_sure.getText().toString().trim() + "(0/" + config.maxNum + ")");
        }
        //确定按钮文字颜色
        if (config.sureBtnTextColor != 0){
            btn_sure.setTextColor(config.sureBtnTextColor);
        }
        //确定按钮背景色
        if (config.sureBtnBgColor != 0){
            btn_sure.setBackgroundColor(config.sureBtnBgColor);
        }else if (config.sureBtnBgResource != 0){//确定按钮背景Resource
            btn_sure.setBackgroundResource(config.sureBtnBgResource);
        }
    }

    /**
     * 设置选择数量text
     */
    private void refreshSelectNumText(){
        if (map_cacheSelectInfo != null && map_cacheSelectInfo.size() > 0){
            btn_sure.setText("完成("+map_cacheSelectInfo.size()+"/"+config.maxNum+")");
        }else{
            btn_sure.setText("完成");
        }

    }

    /**
     * 完成选择
     * @param filePath  不为空的话直接返回此地址，为空返回map_cacheSelectInfo里的数据
     */
    private void finishSelect(String filePath) {
        Intent intent = new Intent();
        ArrayList<String> li_path = new ArrayList<>();
        if (TextUtils.isEmpty(filePath)){
            Set<String> keySet = map_cacheSelectInfo.keySet();
            Iterator<String> iterator = keySet.iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                MediaInfo info = map_cacheSelectInfo.get(key);
                li_path.add(info.filePath);
            }
            intent.putStringArrayListExtra("result", li_path);
        }else{
            li_path.add(filePath);
            intent.putStringArrayListExtra("result", li_path);
        }
        setResult(RESULT_OK, intent);
        if (MediaTempListener.listener != null){
            MediaTempListener.listener.onSelectMedia(li_path);
        }
        finish();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_selectFolder){//文件夹选择
            //如果返回栈大于0，则先返回列表
            if (getSupportFragmentManager().getBackStackEntryCount() > 0){
                onBackPressed();
            }
            //打开文件夹选择
            mediaFragment.openFolderPopupWindow(btn_selectFolder);

        }else if(view.getId() == R.id.btn_sure){//完成选择
            //判断是否有最小选择数量条件
            if (config.multiSelect && config.minNum > 0 && map_cacheSelectInfo.size() < config.minNum){
                showMessage("最少需要选择"+config.minNum+"项");
                return;
            }
            //完成选择
            finishSelect(null);
        }else if(view.getId() == R.id.iv_back){//返回
            onBackPressed();
        }
    }

    /**
     * 启动activity
     * @param mActivity
     * @param intent
     */
    private static void startActivityBottomToTop(Activity mActivity,Intent intent){
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.enter_from_bottom,R.anim.out_to_top);
    }

    //--------------------------------------------- 菜单点击事件回调 ----------------------------------------------------------

    @Override
    public void onItemClick(ArrayList<MediaInfo> li_AllInfo, int position) {
        if (position == -1){//点击列表的照相机
            needCleanWithFinish = false;

            DVCameraConfig cameraConfig = MediaSelectorManager.getDefaultCameraConfig();
            cameraConfig.fileCachePath = config.fileCachePath;
            cameraConfig.mediaType = config.mediaType;
            cameraConfig.needCrop = config.needCrop;
            cameraConfig.cropSize(config.aspectX,config.aspectY,config.outputX,config.outputY);
            MediaSelectorManager.openCameraWithConfig(mContext,cameraConfig, MediaTempListener.listener);

            finish();
            return;
        }
        //判断单选还是多选
        if (config.multiSelect){//多选
            WatchMediaFragment watchMediaFragment = WatchMediaFragment.instance();
            watchMediaFragment.setOnItemClickListener(this);
            Bundle bundle = new Bundle();
            bundle.putSerializable("mediaInfos",li_AllInfo);
            bundle.putInt("firstPosition",position);
            watchMediaFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.anim_show_alpha,R.anim.anim_hidden_alpha)
                    .add(R.id.fl_mediaList, watchMediaFragment, WatchMediaFragment.class.getName())
                    .addToBackStack(WatchMediaFragment.class.getName())
                    .commit();
        }else{//单选
            String filePath = li_AllInfo.get(position).filePath;
            if (config.needCrop && !MediaFileTypeUtils.isVideoFileType(filePath)){//需要裁剪且不是视频文件
                cropImage(filePath);
            }else{
                finishSelect(filePath);
            }
        }

    }

    @Override
    public void onItemCheck(MediaInfo info, boolean isChecked) {
        if (isChecked){
            map_cacheSelectInfo.put(info.filePath,info);
        }else{
            map_cacheSelectInfo.remove(info.filePath);
        }
        refreshSelectNumText();
    }

    @Override
    public boolean itemCheckEnabled(int position, boolean isChecked) {
        if (map_cacheSelectInfo.size() >= config.maxNum && isChecked){
            String end = config.mediaType == DVMediaType.PHOTO ? "张" : "项";
            showMessage("最多只能选择" + config.maxNum + end);
            return false;
        }else {
            return true;
        }
    }

    /**
     * 右上角文件夹选择事件
     * @param folderInfo
     */
    @Override
    public void onFolderCheck(FolderInfo folderInfo){
        btn_selectFolder.setText(""+folderInfo.folderName);
    }

    // ---------------------------------------------- 裁剪图片 --------------------------------------------------
    /**
     * 裁剪图片
     * @param imagePath
     */
    private void cropImage(String imagePath) {
        cropImageFile = new File(fileCachePath + File.separator + System.currentTimeMillis() + ".jpg");

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(getImageContentUri(new File(imagePath)), "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", config.aspectX);
        intent.putExtra("aspectY", config.aspectY);
        intent.putExtra("outputX", config.outputX);
        intent.putExtra("outputY", config.outputY);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cropImageFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);

        startActivityForResult(intent, IMAGE_CROP_CODE);
    }

    public Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                if (cursor != null) {
                    cursor.close();
                }
                return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_CROP_CODE && resultCode == RESULT_OK) {
            finishSelect(cropImageFile.getPath());
        } else {
            onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (needCleanWithFinish){
            overridePendingTransition(R.anim.enter_from_left,R.anim.out_to_right);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (needCleanWithFinish){
            if (map_cacheSelectInfo != null){
                map_cacheSelectInfo.clear();
                map_cacheSelectInfo = null;
            }
            MediaSelectorManager.getInstance().clean();
            MediaTempListener.release();
        }
    }

}
