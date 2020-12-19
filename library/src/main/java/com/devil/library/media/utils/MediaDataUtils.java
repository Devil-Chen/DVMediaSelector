package com.devil.library.media.utils;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.devil.library.media.bean.MediaInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * 获取媒体数据
 */
public class MediaDataUtils {

    /**
     * 读取手机中所有图片信息
     */
    public static void getAllPhotoInfo(final Activity mContext, final OnLoadCallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final HashMap<String,ArrayList<MediaInfo>> allMediaTemp = new HashMap<>();//所有照片
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String[] projImage = { MediaStore.Images.Media._ID
                        , MediaStore.Images.Media.DATA
                        ,MediaStore.Images.Media.SIZE
                        ,MediaStore.Images.Media.DISPLAY_NAME
                        ,MediaStore.Video.Media.DATE_MODIFIED};
                Cursor mCursor = mContext.getContentResolver().query(mImageUri,
                        projImage,
                        null,
                        null,
                        MediaStore.Images.Media.DATE_MODIFIED+" desc");

                if(mCursor!=null){
                    while (mCursor.moveToNext()) {
                        // 获取图片的路径
                        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        int size = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.SIZE))/1024;
                        String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        long modifiedTime =  mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
                        // 获取该图片的父路径名
                        String dirPath = new File(path).getParentFile().getAbsolutePath();
                        //存储对应关系
                        if (allMediaTemp.containsKey(dirPath)) {
                            ArrayList<MediaInfo> data = allMediaTemp.get(dirPath);
                            data.add(MediaInfo.createPhotoInfo(path,size,displayName,modifiedTime));
                        } else {
                            ArrayList<MediaInfo> data = new ArrayList<>();
                            data.add(MediaInfo.createPhotoInfo(path,size,displayName,modifiedTime));
                            allMediaTemp.put(dirPath,data);
                        }
                    }
                    mCursor.close();
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null){
                                callBack.onLoadSuccess(allMediaTemp);
                            }
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 获取手机中所有视频的信息 （不要缩略图能加快加载速度）
     * @param mContext 上下文
     * @param isNeedThumbnail 是否需要视频缩略图
     * @param callBack
     */
    public static void getAllVideoInfo(final Activity mContext, final boolean isNeedThumbnail, final OnLoadCallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final HashMap<String,ArrayList<MediaInfo>> allMediaTemp = new HashMap<>();//所有照片
                Uri mImageUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] proj = { MediaStore.Video.Media._ID
                        , MediaStore.Video.Media.DATA
                        ,MediaStore.Video.Media.DURATION
                        ,MediaStore.Video.Media.SIZE
                        ,MediaStore.Video.Media.DISPLAY_NAME
                        ,MediaStore.Video.Media.DATE_MODIFIED};
                Cursor mCursor = mContext.getContentResolver().query(mImageUri,
                        proj,
                        null ,
                        null,
                        MediaStore.Video.Media.DATE_MODIFIED+" desc");
                if(mCursor!=null){
                    while (mCursor.moveToNext()) {
                        // 获取视频的路径
                        int videoId = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media._ID));
                        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        int duration = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        long size = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.SIZE))/1024; //单位kb
                        long modifiedTime =  mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
                        if(size<0){
                            //某些设备获取size<0，直接计算
                            Log.e("dml","this video size < 0 " + path);
                            size = new File(path).length()/1024;
                        }
                        String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
//                        long modifyTime = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));//暂未用到
                        String thumbPath = "";

                        //判断是否需要缩略图
                        if (isNeedThumbnail){
                            //提前生成缩略图，再获取：http://stackoverflow.com/questions/27903264/how-to-get-the-video-thumbnail-path-and-not-the-bitmap
//                            MediaStore.Video.Thumbnails.getThumbnail(mContext.getContentResolver(), videoId, MediaStore.Video.Thumbnails.MICRO_KIND, null);
                            String[] projection = { MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.DATA};
                            Cursor cursor = mContext.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI
                                    , projection
                                    , MediaStore.Video.Thumbnails.VIDEO_ID + "=?"
                                    , new String[]{videoId+""}
                                    , null);

                            while (cursor.moveToNext()){
                                thumbPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                                //检测缩略图是否存在
                                File thumbFile = new File(thumbPath);
                                if (!thumbFile.exists()){
                                    thumbPath = "";
                                }

                            }
                            cursor.close();
                        }

                        // 获取该视频的父路径名
                        String dirPath = new File(path).getParentFile().getAbsolutePath();
                        //存储对应关系
                        if (allMediaTemp.containsKey(dirPath)) {
                            ArrayList<MediaInfo> data = allMediaTemp.get(dirPath);
                            data.add(MediaInfo.createVideoInfo(path,thumbPath,duration,size,displayName,modifiedTime));
                        } else {
                            ArrayList<MediaInfo> data = new ArrayList<>();
                            data.add(MediaInfo.createVideoInfo(path,thumbPath,duration,size,displayName,modifiedTime));
                            allMediaTemp.put(dirPath,data);
                        }
                    }
                    mCursor.close();
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null){
                                callBack.onLoadSuccess(allMediaTemp);
                            }
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 根据修改时间排序
     * @param sourceArray
     */
    public static void sortByModifiedTime(ArrayList<MediaInfo> sourceArray){
        Collections.sort(sourceArray, new Comparator<MediaInfo>() {
            @Override
            public int compare(MediaInfo o1, MediaInfo o2) {
                if (o2.modifiedTime > o1.modifiedTime){
                    return 1;
                }else if (o2.modifiedTime < o1.modifiedTime){
                    return -1;
                }else{
                    return 0;
                }
            }
        });
    }

    /**
     * 加载回调
     */
    public interface OnLoadCallBack{
        /**
         * 加载成功
         * @param allFiles （key：文件目录 value：文件信息列表）
         */
        void onLoadSuccess(HashMap<String, ArrayList<MediaInfo>> allFiles);
    }
}
