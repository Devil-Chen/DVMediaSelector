package com.devil.library.media.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 权限请求帮助类（需要在AndroidManifest.xml中注册activity-->.PermissionUtil$RequestPermissionsActivity，且最好使用dialog样式android:theme="@style/Theme.AppCompat.Light.Dialog"）
 */
public class PermissionUtils {
    /**通讯录权限*/
    public static final String[] PERMISSION_CONTACTS = new String[]{
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_CONTACTS
    };

    /**电话*/
    public static final String[] PERMISSION_PHONE = new String[]{
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.USE_SIP,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.ADD_VOICEMAIL
    };

    /**日历*/
    public static final String[] PERMISSION_CALENDAR = new String[]{
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
    };

    /**相机*/
    public static final String[] PERMISSION_CAMERA = new String[]{
            Manifest.permission.CAMERA
    };

    /**传感器*/
    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public static final String[] PERMISSION_SENSORS = new String[]{
            Manifest.permission.BODY_SENSORS
    };

    /**位置*/
    public static final String[] PERMISSION_LOCATION = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    /**存储*/
    public static final String[] PERMISSION_FILE_STORAGE = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**麦克风*/
    public static final String[] PERMISSION_MICROPHONE = new String[]{
            Manifest.permission.RECORD_AUDIO
    };

    /**短信*/
    public static final String[] PERMISSION_SMS = new String[]{
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECEIVE_MMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS
    };



    //权限请求使用的回调监听
    private static OnPermissionListener mOnPermissionListener;

    /**
     * 数组合并
     * @param first
     * @param rest
     * @param <T>
     * @return
     */
    public static <T> T[] arrayConcatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    /**
     * 判断是否有某个权限
     * @param context
     * @param permission
     * @return
     */
    public static boolean verifyHasPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否拥有多个权限（不是全部拥有则返回false）
     * @param context
     * @param permissions
     * @return
     */
    public static boolean verifyHasPermission(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 找出所有没有授权的权限
     * @param context
     * @param permissions
     * @return
     */
    public static String[] findNotGrantedPermission(Context context, String[] permissions) {
        List<String> list_str = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    if (list_str == null){
                        list_str = new ArrayList<String>();
                    }
                    list_str.add(permission);
                }
            }
        }
        if (list_str == null){
            return null;
        }else{
            return list_str.toArray(new String[list_str.size()]);
        }
    }

    /**
     * 去请求所有权限
     * @param mContext
     * @param permissions 需要请求的权限列表
     * @param listener 请求权限回调
     */
    public static void requestPermissions(Context mContext, String[] permissions, OnPermissionListener listener) {
        if(mContext == null || listener == null){
            throw new NullPointerException("context参数为空，或者listener参数为空");
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //SDK小于22之前的版本之前发返回权限允许
            listener.onPermissionGranted();
        } else {
            String[] notGrantedPermission = findNotGrantedPermission(mContext,permissions);
            if (notGrantedPermission == null){
                if (listener != null) listener.onPermissionGranted();
            }else{
                //打开一个一像素的activity去请求权限，并回调返回结果
                start(mContext,permissions,listener);
            }

        }
    }



    /**
     * 权限回调
     */
    public interface OnPermissionListener {

        void onPermissionGranted();//授权

        void onPermissionDenied();//拒绝
    }


    /**
     * 启动activity，并带些必要参数过来
     * @param context
     * @param permissions 申请权限列表
     * @param listener 结果回调
     */
    private static void start(Context context, String[] permissions, PermissionUtils.OnPermissionListener listener){
        Intent intent = new Intent(context, RequestPermissionsActivity.class);
        intent.putExtra("permissions",permissions);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        mOnPermissionListener=listener;
    }


    /**
     * 动态请求权限帮助activity，与AppPermissionUtil联用。
     * 注：这个不是我们app的页面，所以不要轻易改动。
     */
    public static class RequestPermissionsActivity extends Activity {

        private int mRequestCode;

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

            //获取界面传过来的值
            getIntentData();
        }

        private void getIntentData(){
            //获取传递过来回调监听
            //传过来的需要申请的权限
            String[] permissions=getIntent().getStringArrayExtra("permissions");
            if(permissions!=null&&permissions.length>0){
                requestPermissions(permissions);
            }else {
                //手动报错提示
                throw new NullPointerException("申请的权限列表不能为空！");
            }
        }

        /**
         * 去申请所有权限
         * @param permissions
         */
        @TargetApi(Build.VERSION_CODES.M)
        private void requestPermissions(String[] permissions){
            Random random=new Random();
            mRequestCode=random.nextInt(1000);
            List<String> deniedPermissions = getDeniedPermissions(permissions);
            if (deniedPermissions.size() > 0) {
                //没有授权过，去申请
                requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), mRequestCode);
            } else {
                //都已经授权过了
                if(mOnPermissionListener!=null)
                    mOnPermissionListener.onPermissionGranted();
                if(!isFinishing()) {
                    finish();
                }
            }
        }

        /**
         * 请求权限结果
         */
        public void requestPermissionsResult(int requestCode, int[] grantResults) {
            if (requestCode != -1 && mRequestCode == requestCode) {
                if (verifyPermissions(grantResults)) {
                    //都授权了
                    if(mOnPermissionListener!=null)
                        mOnPermissionListener.onPermissionGranted();
                    finish();
                } else {
                    //有一个未授权或者多个未授权
                    if(mOnPermissionListener!=null)
                        mOnPermissionListener.onPermissionDenied();
                    finish();
                }
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            //申请权限结果返回
            requestPermissionsResult(requestCode,grantResults);
        }

        /**
         * 获取请求权限中需要授权的权限,有的可能已经授权过了
         */
        private List<String> getDeniedPermissions(String[] permissions) {
            List<String> deniedPermissions = new ArrayList<>();
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                    deniedPermissions.add(permission);
                }
            }
            return deniedPermissions;
        }

        /**
         * 验证所有权限是否都已经授权
         */
        private  boolean verifyPermissions(int[] grantResults) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            mOnPermissionListener=null;
        }


    }
}
