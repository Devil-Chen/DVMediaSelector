package com.devil.library.media.common;

import android.content.Context;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * 自定义图片加载器
 */
public interface ImageLoader extends Serializable {
    void displayImage(Context context, String path, ImageView imageView);
}