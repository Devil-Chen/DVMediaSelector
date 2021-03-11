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
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.devil.library.camera.listener.ClickListener;
import com.devil.library.camera.listener.ErrorListener;
import com.devil.library.camera.listener.JCameraListener;
import com.devil.library.camera.view.DVCameraView;
import com.devil.library.media.MediaSelectorManager;
import com.devil.library.media.R;
import com.devil.library.media.common.MediaTempListener;
import com.devil.library.media.config.DVCameraConfig;
import com.devil.library.media.enumtype.DVMediaType;
import com.devil.library.media.utils.FileUtils;
import com.miyouquan.library.DVPermissionUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * 美颜照相机
 */
public class DVBeautyCameraActivity extends DVBaseActivity {

    private static final String TAG = DVCameraActivity.class.getName();

    //请求码
    private static final int IMAGE_CROP_CODE = 1;

    //上下文
    private Activity mContext;

    //裁剪图片临时创建的文件
    private File cropImageFile;
    //文件临时保存路径
    private String fileCachePath;

    //相机配置
    private DVCameraConfig config;
    //相机显示的view
    private DVCameraView cameraView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        //获取配置
        config = MediaSelectorManager.getInstance().getCurrentCameraConfig();
        if (config == null){
            showMessage("无法获取相机配置");
            onBackPressed();
            return;
        }

        //设置全屏
        fullScreen();
        //设置布局
        setContentView(R.layout.activity_dv_beauty_camera);

        //设置文件缓存路径
        if (TextUtils.isEmpty(config.fileCachePath)){
            fileCachePath = FileUtils.createRootPath(this);
        }else{
            fileCachePath = config.fileCachePath;
        }


        //检查权限并开始
        checkPermissionAndStart();
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
                    //开启相机
                    startCamera();
                }

                @Override
                public void onPermissionDenied() {
                    showMessage(getString(R.string.permission_denied_tip));
                    finish();
                }
            });
        }else{
            //开启相机
            startCamera();
        }
    }

    /**
     * 全屏显示
     */
    private void fullScreen(){
        Window window= getWindow();
        //全屏显示
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        if (Build.VERSION.SDK_INT >= 19) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        } else {
//            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(option);
//        }
    }

    /**
     * 开启相机
     */
    private void startCamera(){
        //相机显示的view
        cameraView = findViewById(R.id.myCameraView);
        cameraView.setVisibility(View.VISIBLE);

        //设置视频保存路径
        cameraView.setSaveVideoPath(fileCachePath);

        //设置只能录像或只能拍照或两种都可以（默认两种都可以）
        if (config.mediaType == DVMediaType.ALL){//都可以
            cameraView.setFeatures(DVCameraView.BUTTON_STATE_BOTH);
        }else if(config.mediaType == DVMediaType.PHOTO){//只拍照
            cameraView.setFeatures(DVCameraView.BUTTON_STATE_ONLY_CAPTURE);
        }else if(config.mediaType == DVMediaType.VIDEO){//只录像
            cameraView.setFeatures(DVCameraView.BUTTON_STATE_ONLY_RECORDER);
        }

        //设置最大录制时长
        cameraView.setMaxDuration(config.maxDuration);

        //设置闪光灯状态
        cameraView.setFlashLightEnable(config.flashLightEnable);

        //初始化
        cameraView.onCreate(mContext);

        //JCameraView监听
        cameraView.setErrorListener(new ErrorListener() {
            @Override
            public void onError() {
                //打开Camera失败回调
                Log.i(TAG, "open camera error");
            }
            @Override
            public void AudioPermissionError() {
                //没有录取权限回调
                Log.i(TAG, "AudioPermissionError");
            }
        });
        cameraView.setCameraListener(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
                Log.i(TAG, "bitmap = " + bitmap.getWidth());
                String savePath = fileCachePath+"/"+System.currentTimeMillis()+".jpg";
                FileUtils.save(bitmap,new File(savePath), Bitmap.CompressFormat.JPEG,false);
                if (config.needCrop){
                    cropImage(savePath);
                }else{
                    finishSelect(savePath);
                }
            }
            @Override
            public void recordSuccess(String url,Bitmap firstFrame) {
                //获取视频路径
                Log.i(TAG, "url = " + url);
                finishSelect(url);
            }
        });

        //左边按钮点击事件
        cameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                onBackPressed();
            }
        });
        //右边按钮点击事件
        cameraView.setRightClickListener(new ClickListener() {
            @Override
            public void onClick() {

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

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraView != null){
            cameraView.onResume();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (cameraView != null){
            cameraView.onPause();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (cameraView != null){
            cameraView.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (cameraView != null){
            cameraView.onStop();
        }
    }

    /**
     * 判断当前系统时间是否在指定时间的范围内
     *
     * @param beginHour
     * 开始小时，例如22
     * @param beginMin
     * 开始小时的分钟数，例如30
     * @param endHour
     * 结束小时，例如 8
     * @param endMin
     * 结束小时的分钟数，例如0
     * @return true表示在范围内，否则false
     */
    public static boolean isCurrentInTimeScope(int beginHour, int beginMin, int endHour, int endMin) {
        boolean result = false;
        final long aDayInMillis = 1000 * 60 * 60 * 24;
        final long currentTimeMillis = System.currentTimeMillis();

        Time now = new Time();
        now.set(currentTimeMillis);

        Time startTime = new Time();
        startTime.set(currentTimeMillis);
        startTime.hour = beginHour;
        startTime.minute = beginMin;

        Time endTime = new Time();
        endTime.set(currentTimeMillis);
        endTime.hour = endHour;
        endTime.minute = endMin;

        if (!startTime.before(endTime)) {
            // 跨天的特殊情况（比如22:00-8:00）
            startTime.set(startTime.toMillis(true) - aDayInMillis);
            result = !now.before(startTime) && !now.after(endTime); // startTime <= now <= endTime
            Time startTimeInThisDay = new Time();
            startTimeInThisDay.set(startTime.toMillis(true) + aDayInMillis);
            if (!now.before(startTimeInThisDay)) {
                result = true;
            }
        } else {
            // 普通情况(比如 8:00 - 14:00)
            result = !now.before(startTime) && !now.after(endTime); // startTime <= now <= endTime
        }
        return result;
    }

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
        overridePendingTransition(R.anim.anim_dv_enter_from_top,R.anim.anim_dv_out_to_bottom);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaSelectorManager.getInstance().clean();
        MediaTempListener.release();
        if (cameraView != null){
            cameraView.onDestroy();
        }
    }
}
