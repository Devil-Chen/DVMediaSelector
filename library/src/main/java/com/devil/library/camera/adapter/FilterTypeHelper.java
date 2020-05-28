package com.devil.library.camera.adapter;

import com.devil.library.media.R;

public class FilterTypeHelper {
    public static int FilterType2Color(String filterName){
        switch (filterName) {
            case "原图":
                return R.color.filter_color_grey_light;
            case "白猫":
            case "黑猫":
            case "日出":
            case "日落":
                return R.color.filter_color_brown_light;
            case "冰冷":
                return R.color.filter_color_blue_dark;
            case "祖母绿":
            case "常青":
                return R.color.filter_color_green_dark;
            case "童话":
                return R.color.filter_color_blue;
            case "拿铁":
            case "浪漫":
            case "樱花":
                return R.color.filter_color_pink;
            case "素描":
                return R.color.filter_color_blue_dark_dark;
            case "健康":
                return R.color.filter_color_red;
            case "甜心":
                return R.color.filter_color_red_dark;
            case "平静":
                return R.color.filter_color_brown;
            default:
                return R.color.filter_color_brown_dark;
        }
    }
}
