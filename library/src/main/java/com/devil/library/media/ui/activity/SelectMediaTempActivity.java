package com.devil.library.media.ui.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.devil.library.media.common.MediaTempListener;
import com.devil.library.media.listener.OnSelectMediaListener;
import com.devil.library.media.MediaSelectorManager;
import com.devil.library.media.R;
import com.devil.library.media.config.DVCameraConfig;
import com.devil.library.media.config.DVListConfig;
import com.devil.library.media.enumtype.DVMediaType;
import com.devil.library.media.utils.FileUtils;
import com.devil.library.media.utils.PermissionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 选择图片的过渡Activity（实际用户并不知道有此界面存在）
 */
@Deprecated
public class SelectMediaTempActivity extends AppCompatActivity {

    //回调code
    private static final int REQUEST_LIST_CODE = 1;
    //回调监听
    private static OnSelectMediaListener listener;

    //配置选项 - 列表
    private DVListConfig listConfig;
    //配置选项 - 相机
    private DVCameraConfig cameraConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //1像素的activity
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);

        //打开选择器
        startAction();
    }

    /**
     * 打开选择器
     */
    private void startAction(){
        if (MediaSelectorManager.getInstance().getImageLoader() == null){
            Toast.makeText(this,"请先调用MediaSelectorManager.getInstance().initLoader()来设置图片加载方式",Toast.LENGTH_SHORT).show();
            Log.e("MediaSelector","请先调用MediaSelectorManager.getInstance().initLoader()来设置图片加载方式");
            onBackPressed();
            return;
        }
        String action = getIntent().getStringExtra("action");
        if (action.equals("mediaList")){
            //配置选项
            listConfig = MediaSelectorManager.getInstance().getCurrentListConfig();

            // 跳转到图片选择器
            startListActivity(this, listConfig, REQUEST_LIST_CODE);
        }else{
            //配置选项
            cameraConfig = MediaSelectorManager.getInstance().getCurrentCameraConfig();

            // 跳转到相机
            checkCameraPermissionAndStart();
        }


    }


    /**
     * 设置监听者
     * @param mListener
     */
    public static void setOnSelectMediaListener(OnSelectMediaListener mListener){
        listener = mListener;
    }

    /**
     * 结果返回
     * @param path
     */
    private void resultCallBack(String path){
        List<String> li_path = new ArrayList<>();
        li_path.add(path);
        if (listener != null){
            listener.onSelectMedia(li_path);
        }
    }
    /**
     * 结果返回
     * @param li_path
     */
    private void resultCallBack(List<String> li_path){
        if (listener != null){
            listener.onSelectMedia(li_path);
        }
    }



    /**
     * 调用列表选择
     * @param target Activity 、 Fragment 、android.app.Fragment
     * @param config 配置
     * @param reqCode 请求码
     */
    public void startListActivity(Object target, DVListConfig config, int reqCode) {
//        if (target instanceof Activity) {
//            DVListActivity.startForResult((Activity) target, config, reqCode);
//        } else if (target instanceof Fragment) {
//            DVListActivity.startForResult((Fragment) target, config, reqCode);
//        } else if (target instanceof android.app.Fragment) {
//            DVListActivity.startForResult((android.app.Fragment) target, config, reqCode);
//        }
        Intent intent = new Intent(this,DVMediaSelectActivity.class);
        startActivityForResult(intent,reqCode);
    }

    //----------------------------------- 结果回调 ----------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean needFinishActivity = true;
        // 选择结果回调
        if (requestCode == REQUEST_LIST_CODE && resultCode == RESULT_OK && data != null) {//媒体库中选择
            resultCallBack(data.getStringArrayListExtra("result"));
        } else if (requestCode == IMAGE_CROP_CODE && resultCode == RESULT_OK) {//裁剪图片回调
            resultCallBack(cropImageFile.getPath());
        } else if (requestCode == REQUEST_CAMERA) {//拍照回调
            if (resultCode == Activity.RESULT_OK) {
                if (tempPhotoFile != null) {
                    if (cameraConfig.needCrop) {//需要裁剪，进入裁剪界面
                        needFinishActivity = false;
                        cropImage(tempPhotoFile.getAbsolutePath());
                    } else {
                        resultCallBack(tempPhotoFile.getPath());
                    }
                }
            } else {
                //如果不是成功状态，删除缓存文件
                if (tempPhotoFile != null && tempPhotoFile.exists()) {
                    tempPhotoFile.delete();
                }
            }
        }else if(requestCode == REQUEST_VIDEO){//录像回调
            if (resultCode == Activity.RESULT_OK) {
                if (cameraConfig.isUseSystemCamera){//是否使用系统相机
                    if (tempVideoFile != null) {
                        resultCallBack(tempVideoFile.getPath());
                    }
                }else{
                    resultCallBack(data.getStringExtra("result"));
                }

            } else {
                //如果不是成功状态，删除缓存文件
                if (tempVideoFile != null && tempVideoFile.exists()) {
                    tempVideoFile.delete();
                }
            }

        }
        //结束界面
        if (needFinishActivity){
            onBackPressed();
        }
    }

    // -------------------------------------- 相机调用相关 --------------------------------------
    //拍照请求code
    private static final int REQUEST_CAMERA = 5;
    //录像请求code
    private static final int REQUEST_VIDEO = 6;
    //裁剪图片请求code
    private static final int IMAGE_CROP_CODE = 4;

    //临时裁剪图片存放位置
    private File cropImageFile;
    //临时拍照存放位置
    private File tempPhotoFile;
    //临时录像存放位置
    private File tempVideoFile;

    //文件存储跟目录
    private String fileCachePath;

    /**
     * 检查相机权限并开始
     */
    private void checkCameraPermissionAndStart(){
        cameraConfig = MediaSelectorManager.getInstance().getCurrentCameraConfig();
        //设置文件缓存路径
        if (TextUtils.isEmpty(cameraConfig.fileCachePath)){
            fileCachePath = FileUtils.createRootPath(this);
        }else{
            fileCachePath = cameraConfig.fileCachePath;
        }
        //判断是否有权限操作
        String[] permissions = PermissionUtils.arrayConcatAll(PermissionUtils.PERMISSION_CAMERA,PermissionUtils.PERMISSION_FILE_STORAGE,PermissionUtils.PERMISSION_MICROPHONE);
        if (!PermissionUtils.verifyHasPermission(this,permissions)){
            PermissionUtils.requestPermissions(this, permissions, new PermissionUtils.OnPermissionListener() {
                @Override
                public void onPermissionGranted() {
                    //开启相机
                    startCamera();
                }

                @Override
                public void onPermissionDenied() {
                    onBackPressed();
                }
            });
        }else{
            //开启相机
            startCamera();
        }


    }

    /**
     * 初始化相机并且跳转
     */
    private void startCamera(){
        if (cameraConfig.isUseSystemCamera){
            if (cameraConfig.mediaType == DVMediaType.PHOTO){
                takePhoto();
            }else{
                takeVideo();
            }
        }else{
            startDVCameraActivity(REQUEST_VIDEO);
        }
    }

    /**
     * 拍照
     */
    private void takePhoto(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            tempPhotoFile = new File(fileCachePath + "/" + System.currentTimeMillis() + ".jpg");

            FileUtils.createFile(tempPhotoFile);

            Uri uri = FileProvider.getUriForFile(this,
                    FileUtils.getApplicationId(this) + ".file_provider", tempPhotoFile);

            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri); //Uri.fromFile(tempFile)
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else {
            Toast.makeText(this, getResources().getString(com.devil.library.media.R.string.open_camera_failure), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 拍摄视频
     */
    private void takeVideo(){
        Intent intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        Uri fileUri = null;
        try {

            fileUri = FileProvider.getUriForFile(this,getApplicationContext().getPackageName() + ".file_provider",createMediaFile());//这是正确的写法

        } catch (IOException e) {
            e.printStackTrace();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);

        startActivityForResult(intent,REQUEST_VIDEO);
    }

    /**
     * 开启仿微信相机界面
     * @param requestCode 请求码
     */
    private void startDVCameraActivity(int requestCode){
        Intent intent = new Intent(this,DVCameraActivity.class);
        startActivityForResult(intent,requestCode);
    }

    /**
     * 创建录像临时文件
     * @return
     * @throws IOException
     */
    private File createMediaFile() throws IOException {
        if(FileUtils.checkSdCardAvailable()) {
            // 文件根据当前的毫秒数给自己命名
            String timeStamp = String.valueOf(System.currentTimeMillis());
            timeStamp = timeStamp.substring(7);
            String imageFileName = "video" + timeStamp;
            String suffix = ".mp4";
            tempVideoFile = new File(fileCachePath + File.separator + imageFileName + suffix);
            return tempVideoFile;
        }
        return null;
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
        intent.putExtra("aspectX", cameraConfig.aspectX);
        intent.putExtra("aspectY", cameraConfig.aspectY);
        intent.putExtra("outputX", cameraConfig.outputX);
        intent.putExtra("outputY", cameraConfig.outputY);
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
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter_from_left,R.anim.out_to_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaSelectorManager.getInstance().clean();
        MediaTempListener.release();
    }

}
