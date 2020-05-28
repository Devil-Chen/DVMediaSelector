package com.devil.library.camera.listener;


import com.devil.library.camera.params.RecordInfo;

/**
 * 录制状态监听
 */
public interface OnRecordStateListener {

    // 录制开始
    void onRecordStart();

    // 录制进度
    void onRecording(long duration);

    // 录制结束
    void onRecordFinish(RecordInfo info);
}
