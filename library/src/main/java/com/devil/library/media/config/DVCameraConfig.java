package com.devil.library.media.config;

import com.devil.library.media.enumtype.DVMediaType;
import com.devil.library.media.utils.FileUtils;

import java.io.Serializable;

/**
 * 调用相机的配置
 */
public class DVCameraConfig implements Serializable {

    /**
     * 是否需要裁剪
     */
    public boolean needCrop;

    /**
     * 文件缓存路径
     */
    public String fileCachePath;

    /**
     * 裁剪输出大小
     */
    public int aspectX = 1;
    public int aspectY = 1;
    public int outputX = 500;
    public int outputY = 500;

    /**
     * 相机可选择的类型（图片/视频，默认全部）
     */
    public DVMediaType mediaType = DVMediaType.ALL;

    /**
     * 是否使用系统照相机
     */
    public boolean isUseSystemCamera = false;

    public DVCameraConfig needCrop(boolean needCrop) {
        this.needCrop = needCrop;
        return this;
    }

    public DVCameraConfig fileCachePath(String fileCachePath) {
        this.fileCachePath = fileCachePath;
        FileUtils.createDir(fileCachePath);
        return this;
    }

    public DVCameraConfig cropSize(int aspectX, int aspectY, int outputX, int outputY) {
        this.aspectX = aspectX;
        this.aspectY = aspectY;
        this.outputX = outputX;
        this.outputY = outputY;
        return this;
    }

    public DVCameraConfig aspectX(int aspectX) {
        this.aspectX = aspectX;
        return this;
    }

    public DVCameraConfig aspectY(int aspectY) {
        this.aspectY = aspectY;
        return this;
    }

    public DVCameraConfig outputX(int outputX) {
        this.outputX = outputX;
        return this;
    }

    public DVCameraConfig outputY(int outputY) {
        this.outputY = outputY;
        return this;
    }

    public DVCameraConfig mediaType(DVMediaType mediaType){
        this.mediaType = mediaType;
        return this;
    }

    public DVCameraConfig isUseSystemCamera(boolean isUseSystemCamera){
        this.isUseSystemCamera = isUseSystemCamera;
        return this;
    }

    /**
     * 创建默认实例
     * @return
     */
    public static DVCameraConfig createInstance(){
        return new DVCameraConfig();
    }

}
