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

    /**
     * 最大录制时长（单位秒，默认10秒）
     */
    public int maxDuration = 10;

    /**
     * 是否启用闪光灯
     */
    public boolean flashLightEnable = true;

    /**
     * 创建实例Builder
     */
    public static class Builder{
        /**
         * 是否需要裁剪
         */
        private boolean needCrop;

        /**
         * 文件缓存路径
         */
        private String fileCachePath;

        /**
         * 裁剪输出大小
         */
        private int aspectX = 1;
        private int aspectY = 1;
        private int outputX = 500;
        private int outputY = 500;

        /**
         * 相机可选择的类型（图片/视频，默认全部）
         */
        private DVMediaType mediaType = DVMediaType.ALL;

        /**
         * 是否使用系统照相机
         */
        private boolean isUseSystemCamera = false;

        /**
         * 最大录制时长（单位秒，默认10秒）
         */
        private int maxDuration = 10;

        /**
         * 是否启用闪光灯（启用则后置摄像头显示，前置摄像头不显示）
         */
        private boolean flashLightEnable = true;


        public Builder needCrop(boolean needCrop) {
            this.needCrop = needCrop;
            return this;
        }

        public Builder fileCachePath(String fileCachePath) {
            this.fileCachePath = fileCachePath;
            FileUtils.createDir(fileCachePath);
            return this;
        }

        public Builder cropSize(int aspectX, int aspectY, int outputX, int outputY) {
            this.aspectX = aspectX;
            this.aspectY = aspectY;
            this.outputX = outputX;
            this.outputY = outputY;
            return this;
        }

        public Builder aspectX(int aspectX) {
            this.aspectX = aspectX;
            return this;
        }

        public Builder aspectY(int aspectY) {
            this.aspectY = aspectY;
            return this;
        }

        public Builder outputX(int outputX) {
            this.outputX = outputX;
            return this;
        }

        public Builder outputY(int outputY) {
            this.outputY = outputY;
            return this;
        }

        public Builder mediaType(DVMediaType mediaType){
            this.mediaType = mediaType;
            return this;
        }

        public Builder isUseSystemCamera(boolean isUseSystemCamera){
            this.isUseSystemCamera = isUseSystemCamera;
            return this;
        }
        public Builder maxDuration(int maxDuration){
            this.maxDuration = maxDuration;
            return this;
        }
        public Builder flashLightEnable(boolean enable){
            this.flashLightEnable = enable;
            return this;
        }

        /**
         * 获取配置后的实例
         * @return
         */
        public DVCameraConfig build(){
            DVCameraConfig config = new DVCameraConfig();
            /**
             * 是否需要裁剪
             */
            config.needCrop = needCrop;

            /**
             * 文件缓存路径
             */
            config.fileCachePath = fileCachePath;

            /**
             * 裁剪输出大小
             */
            config.aspectX = aspectX;
            config.aspectY = aspectY;
            config.outputX = outputX;
            config.outputY = outputY;

            /**
             * 相机可选择的类型（图片/视频，默认全部）
             */
            config.mediaType = mediaType;

            /**
             * 是否使用系统照相机
             */
            config.isUseSystemCamera = isUseSystemCamera;
            /**
             * 最大录制时长（单位秒，默认10秒）
             */
            config.maxDuration = maxDuration;
            /**
             * 是否启用闪光灯
             */
            config.flashLightEnable = flashLightEnable;

            return config;
        }

    }




}
