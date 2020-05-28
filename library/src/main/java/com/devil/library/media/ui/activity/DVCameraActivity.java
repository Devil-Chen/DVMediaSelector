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
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.devil.library.camera.JCameraView;
import com.devil.library.camera.listener.ClickListener;
import com.devil.library.camera.listener.ErrorListener;
import com.devil.library.camera.listener.JCameraListener;
import com.devil.library.media.R;
import com.devil.library.media.MediaSelectorManager;
import com.devil.library.media.common.MediaTempListener;
import com.devil.library.media.config.DVCameraConfig;
import com.devil.library.media.enumtype.DVMediaType;
import com.devil.library.media.utils.FileUtils;
import com.miyouquan.library.DVPermissionUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * 打开照相机（自定义）
 */
public class DVCameraActivity extends DVBaseActivity {

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
    private JCameraView cameraView;
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
        setContentView(R.layout.activity_dv_camera);

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

        //设置视频保存路径
        cameraView.setSaveVideoPath(fileCachePath);

        //设置只能录像或只能拍照或两种都可以（默认两种都可以）
        if (config.mediaType == DVMediaType.ALL){//都可以
            cameraView.setFeatures(JCameraView.BUTTON_STATE_BOTH);
        }else if(config.mediaType == DVMediaType.PHOTO){//只拍照
            cameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_CAPTURE);
        }else if(config.mediaType == DVMediaType.VIDEO){//只录像
            cameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_RECORDER);
        }

        //设置最大录制时长
        cameraView.setMaxDuration(config.maxDuration);

        //设置视频质量
        cameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);

        //设置闪光灯状态
        cameraView.setFlashLightEnable(config.flashLightEnable);

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
        cameraView.setJCameraLisenter(new JCameraListener() {
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
            public void recordSuccess(String url, Bitmap firstFrame) {
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
        overridePendingTransition(R.anim.enter_from_top,R.anim.out_to_bottom);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaSelectorManager.getInstance().clean();
        MediaTempListener.release();
    }
}
