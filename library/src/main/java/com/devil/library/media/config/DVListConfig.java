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
     * 创建默认实例
     * @return
     */
    public static DVListConfig createInstance(){

        return new DVListConfig();
    }

    public DVListConfig needCrop(boolean needCrop) {
        this.needCrop = needCrop;
        return this;
    }

    public DVListConfig multiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
        return this;
    }

    public DVListConfig maxNum(int maxNum) {
        this.maxNum = maxNum;
        return this;
    }

    public DVListConfig minNum(int minNum) {
        this.minNum = minNum;
        return this;
    }


    public DVListConfig needCamera(boolean needCamera) {
        this.needCamera = needCamera;
        return this;
    }

    public DVListConfig cameraIconResource(int cameraIconResource) {
        this.cameraIconResource = cameraIconResource;
        return this;
    }

    public DVListConfig checkIconResource(int checkIconResource) {
        this.checkIconResource = checkIconResource;
        return this;
    }

    public DVListConfig unCheckIconResource(int unCheckIconResource) {
        this.unCheckIconResource = unCheckIconResource;
        return this;
    }

    public DVListConfig statusBarColor(int statusBarColor) {
        this.statusBarColor = statusBarColor;
        return this;
    }

    public DVListConfig backResourceId(int backResourceId) {
        this.backResourceId = backResourceId;
        return this;
    }

    public DVListConfig title(String title) {
        this.title = title;
        return this;
    }

    public DVListConfig titleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
        return this;
    }

    public DVListConfig titleBgColor(int titleBgColor) {
        this.titleBgColor = titleBgColor;
        return this;
    }

    public DVListConfig sureBtnText(String sureBtnText) {
        this.sureBtnText = sureBtnText;
        return this;
    }

    public DVListConfig sureBtnTextColor(int sureBtnTextColor) {
        this.sureBtnTextColor = sureBtnTextColor;
        return this;
    }

    public DVListConfig sureBtnBgColor(int sureBtnBgColor) {
        this.sureBtnBgColor = sureBtnBgColor;
        return this;
    }

    public DVListConfig sureBtnBgResource(int sureBtnBgResource) {
        this.sureBtnBgResource = sureBtnBgResource;
        return this;
    }

    public DVListConfig sureBtnLayoutBgColor(int sureBtnLayoutBgColor) {
        this.sureBtnLayoutBgColor = sureBtnLayoutBgColor;
        return this;
    }

    public DVListConfig sureBtnLayoutBgResource(int sureBtnLayoutBgResource) {
        this.sureBtnLayoutBgResource = sureBtnLayoutBgResource;
        return this;
    }

    public DVListConfig fileCachePath(String fileCachePath) {
        this.fileCachePath = fileCachePath;
        FileUtils.createDir(fileCachePath);
        return this;
    }

    public DVListConfig cropSize(int aspectX, int aspectY, int outputX, int outputY) {
        this.aspectX = aspectX;
        this.aspectY = aspectY;
        this.outputX = outputX;
        this.outputY = outputY;
        return this;
    }

    public DVListConfig aspectX(int aspectX) {
        this.aspectX = aspectX;
        return this;
    }

    public DVListConfig aspectY(int aspectY) {
        this.aspectY = aspectY;
        return this;
    }

    public DVListConfig outputX(int outputX) {
        this.outputX = outputX;
        return this;
    }

    public DVListConfig outputY(int outputY) {
        this.outputY = outputY;
        return this;
    }

    public DVListConfig listSpanCount(int listSpanCount) {
        this.listSpanCount = listSpanCount;
        return this;
    }

    public DVListConfig statusBarLightMode(boolean statusBarLightMode) {
        this.statusBarLightMode = statusBarLightMode;
        return this;
    }

    public DVListConfig statusBarDrakMode(boolean statusBarDrakMode) {
        this.statusBarDrakMode = statusBarDrakMode;
        return this;
    }

    public DVListConfig mediaType(DVMediaType mediaType){
        this.mediaType = mediaType;
        return this;
    }

    public DVListConfig rightTitleTextColor(int rightTitleTextColor){
        this.rightTitleTextColor = rightTitleTextColor;
        return this;
    }
    public DVListConfig rigntTitleText(String rigntTitleText){
        this.rigntTitleText = rigntTitleText;
        return this;
    }
    public DVListConfig rightTitleVisibility(int rightTitleVisibility){
        this.rightTitleVisibility = rightTitleVisibility;
        return this;
    }

}
