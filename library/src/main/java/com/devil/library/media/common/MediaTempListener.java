package com.devil.library.media.common;

import com.devil.library.media.listener.OnSelectMediaListener;

/**
 * 设置回调监听者（临时）
 */
public class MediaTempListener {
    //回调监听
    public static OnSelectMediaListener listener;

    /**
     * 设置监听者
     * @param mListener
     */
    public static void setOnSelectMediaListener(OnSelectMediaListener mListener){
        listener = mListener;
    }

    /**
     * 释放监听者
     */
    public static void release(){
        if (listener != null){
            listener = null;
        }
    }
}
