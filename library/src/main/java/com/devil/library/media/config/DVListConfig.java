package com.devil.library.media.config;

import android.view.View;

import com.devil.library.media.enumtype.DVMediaType;
import com.devil.library.media.utils.FileUtils;

import java.io.Serializable;

/**
 * 图片列表配置
 */
public class DVListConfig implements Serializable {

    /**
     * 是否需要裁剪
     */
    public boolean needCrop;

    /**
     * 是否多选
     */
    public boolean multiSelect = false;

    /**
     * 最多选择图片数
     */
    public int maxNum = 9;

    /**
     * 最少选择图片数
     */
    public int minNum = 0;

    /**
     * 第一个item是否显示相机（只有单选才有效）
     */
    public boolean needCamera;

    /**
     * 第一个item显示的相机图标（只有单选才有效）
     */
    public int cameraIconResource;

    /**
     * 列表选中的图标
     */
    public int checkIconResource;

    /**
     * 列表未选中的图标
     */
    public int unCheckIconResource;

    /**
     * 状态栏颜色
     */
    public int statusBarColor ;
    /**
     * 状态栏mode
     */
    public boolean statusBarLightMode = false;
    public boolean statusBarDrakMode = false;

    /**
     * 返回图标资源
     */
    public int backResourceId ;

    /**
     * 标题
     */
    public String title;

    /**
     * 标题颜色
     */
    public int titleTextColor;

    /**
     * titlebar背景色
     */
    public int titleBgColor;

    /**
     * 确定按钮文字
     */
    public String sureBtnText;

    /**
     * 确定按钮文字颜色
     */
    public int sureBtnTextColor;

    /**
     * 确定按钮背景色（与Resource只能选择一种）
     */
    public int sureBtnBgColor;

    /**
     * 确定按钮背景色（与color只能选择一种）
     */
    public int sureBtnBgResource;

    /**
     * 确定按钮所在布局背景色（与Resource只能选择一种）
     */
    public int sureBtnLayoutBgColor;

    /**
     * 确定按钮所在布局背景色（与color只能选择一种）
     */
    public int sureBtnLayoutBgResource;


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
     * 列表每行显示的数量
     */
    public int listSpanCount = 3;

    /**
     * 选择的类型（图片/视频）
     */
    public DVMediaType mediaType = DVMediaType.PHOTO;

    /**
     * 右边标题字体颜色
     */
    public int rightTitleTextColor;
    /**
     * 右边标题内容
     */
    public String rigntTitleText;
    /**
     * 是否显示右边标题
     */
    public int rightTitleVisibility = View.VISIBLE;
    /**
     * 是否需要预览
     */
    public boolean hasPreview = true;










    /**
     * 创建实例Builder
     */
    public static class Builder{
        /**
         * 是否需要裁剪
         */
        private boolean needCrop;

        /**
         * 是否多选
         */
        private boolean multiSelect = false;

        /**
         * 最多选择图片数
         */
        private int maxNum = 9;

        /**
         * 最少选择图片数
         */
        private int minNum = 0;

        /**
         * 第一个item是否显示相机（只有单选才有效）
         */
        private boolean needCamera;

        /**
         * 第一个item显示的相机图标（只有单选才有效）
         */
        private int cameraIconResource;

        /**
         * 列表选中的图标
         */
        private int checkIconResource;

        /**
         * 列表未选中的图标
         */
        private int unCheckIconResource;

        /**
         * 状态栏颜色
         */
        private int statusBarColor ;
        /**
         * 状态栏mode
         */
        private boolean statusBarLightMode = false;
        private boolean statusBarDrakMode = false;

        /**
         * 返回图标资源
         */
        private int backResourceId ;

        /**
         * 标题
         */
        private String title;

        /**
         * 标题颜色
         */
        private int titleTextColor;

        /**
         * titlebar背景色
         */
        private int titleBgColor;

        /**
         * 确定按钮文字
         */
        private String sureBtnText;

        /**
         * 确定按钮文字颜色
         */
        private int sureBtnTextColor;

        /**
         * 确定按钮背景色（与Resource只能选择一种）
         */
        private int sureBtnBgColor;

        /**
         * 确定按钮背景色（与color只能选择一种）
         */
        private int sureBtnBgResource;

        /**
         * 确定按钮所在布局背景色（与Resource只能选择一种）
         */
        private int sureBtnLayoutBgColor;

        /**
         * 确定按钮所在布局背景色（与color只能选择一种）
         */
        private int sureBtnLayoutBgResource;


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
         * 列表每行显示的数量
         */
        private int listSpanCount = 3;

        /**
         * 选择的类型（图片/视频）
         */
        private DVMediaType mediaType = DVMediaType.PHOTO;

        /**
         * 右边标题字体颜色
         */
        private int rightTitleTextColor;
        /**
         * 右边标题内容
         */
        private String rigntTitleText;
        /**
         * 是否显示右边标题
         */
        private int rightTitleVisibility = View.VISIBLE;
        /**
         * 是否需要预览
         */
        private boolean hasPreview = true;


        public Builder needCrop(boolean needCrop) {
            this.needCrop = needCrop;
            return this;
        }

        public Builder multiSelect(boolean multiSelect) {
            this.multiSelect = multiSelect;
            return this;
        }

        public Builder maxNum(int maxNum) {
            this.maxNum = maxNum;
            return this;
        }

        public Builder minNum(int minNum) {
            this.minNum = minNum;
            return this;
        }


        public Builder needCamera(boolean needCamera) {
            this.needCamera = needCamera;
            return this;
        }

        public Builder cameraIconResource(int cameraIconResource) {
            this.cameraIconResource = cameraIconResource;
            return this;
        }

        public Builder checkIconResource(int checkIconResource) {
            this.checkIconResource = checkIconResource;
            return this;
        }

        public Builder unCheckIconResource(int unCheckIconResource) {
            this.unCheckIconResource = unCheckIconResource;
            return this;
        }

        public Builder statusBarColor(int statusBarColor) {
            this.statusBarColor = statusBarColor;
            return this;
        }

        public Builder backResourceId(int backResourceId) {
            this.backResourceId = backResourceId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder titleTextColor(int titleTextColor) {
            this.titleTextColor = titleTextColor;
            return this;
        }

        public Builder titleBgColor(int titleBgColor) {
            this.titleBgColor = titleBgColor;
            return this;
        }

        public Builder sureBtnText(String sureBtnText) {
            this.sureBtnText = sureBtnText;
            return this;
        }

        public Builder sureBtnTextColor(int sureBtnTextColor) {
            this.sureBtnTextColor = sureBtnTextColor;
            return this;
        }

        public Builder sureBtnBgColor(int sureBtnBgColor) {
            this.sureBtnBgColor = sureBtnBgColor;
            return this;
        }

        public Builder sureBtnBgResource(int sureBtnBgResource) {
            this.sureBtnBgResource = sureBtnBgResource;
            return this;
        }

        public Builder sureBtnLayoutBgColor(int sureBtnLayoutBgColor) {
            this.sureBtnLayoutBgColor = sureBtnLayoutBgColor;
            return this;
        }

        public Builder sureBtnLayoutBgResource(int sureBtnLayoutBgResource) {
            this.sureBtnLayoutBgResource = sureBtnLayoutBgResource;
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

        public Builder listSpanCount(int listSpanCount) {
            this.listSpanCount = listSpanCount;
            return this;
        }

        public Builder statusBarLightMode(boolean statusBarLightMode) {
            this.statusBarLightMode = statusBarLightMode;
            return this;
        }

        public Builder statusBarDrakMode(boolean statusBarDrakMode) {
            this.statusBarDrakMode = statusBarDrakMode;
            return this;
        }

        public Builder mediaType(DVMediaType mediaType){
            this.mediaType = mediaType;
            return this;
        }

        public Builder rightTitleTextColor(int rightTitleTextColor){
            this.rightTitleTextColor = rightTitleTextColor;
            return this;
        }
        public Builder rigntTitleText(String rigntTitleText){
            this.rigntTitleText = rigntTitleText;
            return this;
        }
        public Builder rightTitleVisibility(int rightTitleVisibility){
            this.rightTitleVisibility = rightTitleVisibility;
            return this;
        }
        public Builder hasPreview(boolean hasPreview){
            this.hasPreview = hasPreview;
            return this;
        }


        /**
         * 获取配置后的实例
         * @return
         */
        public DVListConfig build(){
            DVListConfig config = new DVListConfig();

            /**
             * 是否需要裁剪
             */
            config.needCrop = needCrop;

            /**
             * 是否多选
             */
            config.multiSelect = multiSelect;

            /**
             * 最多选择图片数
             */
            config.maxNum = maxNum;

            /**
             * 最少选择图片数
             */
            config.minNum = minNum;

            /**
             * 第一个item是否显示相机（只有单选才有效）
             */
            config.needCamera = needCamera;

            /**
             * 第一个item显示的相机图标（只有单选才有效）
             */
            config.cameraIconResource = cameraIconResource;

            /**
             * 列表选中的图标
             */
            config.checkIconResource = checkIconResource;

            /**
             * 列表未选中的图标
             */
            config.unCheckIconResource = unCheckIconResource;

            /**
             * 状态栏颜色
             */
            config.statusBarColor = statusBarColor;
            /**
             * 状态栏mode
             */
            config.statusBarLightMode = statusBarLightMode;
            config.statusBarDrakMode = statusBarDrakMode;

            /**
             * 返回图标资源
             */
            config.backResourceId = backResourceId;

            /**
             * 标题
             */
            config.title = title;

            /**
             * 标题颜色
             */
            config.titleTextColor = titleTextColor;

            /**
             * titlebar背景色
             */
            config.titleBgColor = titleBgColor;

            /**
             * 确定按钮文字
             */
            config.sureBtnText = sureBtnText;

            /**
             * 确定按钮文字颜色
             */
            config.sureBtnTextColor = sureBtnTextColor;

            /**
             * 确定按钮背景色（与Resource只能选择一种）
             */
            config.sureBtnBgColor = sureBtnBgColor;

            /**
             * 确定按钮背景色（与color只能选择一种）
             */
            config.sureBtnBgResource = sureBtnBgResource;

            /**
             * 确定按钮所在布局背景色（与Resource只能选择一种）
             */
            config.sureBtnLayoutBgColor = sureBtnLayoutBgColor;

            /**
             * 确定按钮所在布局背景色（与color只能选择一种）
             */
            config.sureBtnLayoutBgResource = sureBtnLayoutBgResource;


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
             * 列表每行显示的数量
             */
            config.listSpanCount = listSpanCount;

            /**
             * 选择的类型（图片/视频）
             */
            config.mediaType = mediaType;

            /**
             * 右边标题字体颜色
             */
            config.rightTitleTextColor = rightTitleTextColor;
            /**
             * 右边标题内容
             */
            config.rigntTitleText = rigntTitleText;
            /**
             * 是否显示右边标题
             */
            config.rightTitleVisibility = rightTitleVisibility;
            /**
             * 是否需要预览
             */
            config.hasPreview = hasPreview;

            return config;
        }

    }



}
