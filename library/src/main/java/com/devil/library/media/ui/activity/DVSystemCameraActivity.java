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
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import com.devil.library.media.R;
import com.devil.library.media.MediaSelectorManager;
import com.devil.library.media.common.MediaTempListener;
import com.devil.library.media.config.DVCameraConfig;
import com.devil.library.media.enumtype.DVMediaType;
import com.devil.library.media.utils.FileUtils;
import com.miyouquan.library.DVPermissionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 打开照相机（系统）
 */
public class DVSystemCameraActivity extends DVBaseActivity {

    private static final int REQUEST_CAMERA = 5;
    private static final int REQUEST_VIDEO = 6;
    private static final int IMAGE_CROP_CODE = 1;

    //上下文
    private Activity mContext;

    private File cropImageFile;
    private File tempPhotoFile;
    private File tempVideoFile;

    private DVCameraConfig config;

    //文件临时保存路径
    private String fileCachePath;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mContext = this;

        config = MediaSelectorManager.getInstance().getCurrentCameraConfig();
        if (config == null)
            return;

        checkPermissionAndStart();
    }
    /**
     * 检查权限并开始
     */
    private void checkPermissionAndStart() {
        //设置文件缓存路径
        if (TextUtils.isEmpty(config.fileCachePath)){
            fileCachePath = FileUtils.createRootPath(this);
        }else{
            fileCachePath = config.fileCachePath;
        }
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
     * 开启相机
     */
    private void startCamera(){
        if (config.mediaType == DVMediaType.PHOTO){
            takePhoto();
        }else{
            takeVideo();
        }
    }

    /**
     * 拍照
     */
    private void takePhoto(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            tempPhotoFile = new File(fileCachePath + File.separator + System.currentTimeMillis() + ".jpg");
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

    private File createMediaFile() throws IOException {
        if(FileUtils.checkSdCardAvailable()) {

            // 文件根据当前的毫秒数给自己命名
            String timeStamp = String.valueOf(System.currentTimeMillis());
            timeStamp = timeStamp.substring(7);
            String imageFileName = "V" + timeStamp;
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
    private void crop(String imagePath) {
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
        } else if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (tempPhotoFile != null) {
                    if (config.needCrop) {
                        crop(tempPhotoFile.getAbsolutePath());
                    } else {
                        // complete(new Image(cropImageFile.getPath(), cropImageFile.getName()));
                        finishSelect(tempPhotoFile.getPath());
                    }
                }
            } else {
                if (tempPhotoFile != null && tempPhotoFile.exists()) {
                    tempPhotoFile.delete();
                }
                onBackPressed();
            }
        }else if(requestCode == REQUEST_VIDEO){
            if (resultCode == Activity.RESULT_OK) {
                if (tempVideoFile != null) {
                    finishSelect(tempVideoFile.getPath());
                }
            } else {
                if (tempVideoFile != null && tempVideoFile.exists()) {
                    tempVideoFile.delete();
                }
                onBackPressed();
            }

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
    }
}
