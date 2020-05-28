package com.devil.library.camera.listener;

import com.devil.library.camera.params.MediaType;
import com.devil.library.camera.params.RecordInfo;

/**
 * 录制监听器, MediaRecorder内部使用
 */
public interface OnRecordListener {

    // 录制开始
    void onRecordStart(MediaType type);

    // 录制进度
    void onRecording(MediaType type, long duration);

    // 录制完成
    void onRecordFinish(RecordInfo info);
}
