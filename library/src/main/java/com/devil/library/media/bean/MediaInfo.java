package com.devil.library.media.bean;

import android.util.Log;

import java.io.Serializable;

/**
 * 资源信息实体
 */
public class MediaInfo implements Serializable {
    //资源路径
    public String filePath;
    //视频缩略图
    public String thumbPath;
    //视频时长
    public int duration;
    //文件大小，单位kb
    public  long fileSize;
    //文件名称
    public String fileName;
    //最后修改时间
    public long modifiedTime;

    /**
     * 创建视频信息实体
     * @param filePath 文件路径
     * @param thumbPath 视频缩略图路径
     * @param duration 视频时长
     * @param fileSize 文件大小
     * @param fileName 文件名称
     * @return
     */
    public static MediaInfo createVideoInfo(String filePath,String thumbPath,int duration, long fileSize,String fileName,long modifiedTime){
        MediaInfo info = new MediaInfo();
        info.filePath = filePath;
        info.thumbPath = thumbPath;
        info.duration = duration;
        info.fileSize = fileSize;
        info.fileName = fileName;
        info.modifiedTime = modifiedTime;
        return info;
    }

    /**
     * 创建图片信息实体
     * @param filePath 文件路径
     * @param fileSize 文件大小
     * @param fileName 文件名称
     * @return
     */
    public static MediaInfo createPhotoInfo(String filePath, long fileSize,String fileName,long modifiedTime){
        MediaInfo info = new MediaInfo();
        info.filePath = filePath;
        info.thumbPath = filePath;
        info.fileSize = fileSize;
        info.fileName = fileName;
        info.modifiedTime = modifiedTime;
        return info;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MediaInfo){
            MediaInfo target = (MediaInfo) obj;
            if (filePath.equals(target.filePath)){
                return true;
            }
        }
        return super.equals(obj);
    }
}
