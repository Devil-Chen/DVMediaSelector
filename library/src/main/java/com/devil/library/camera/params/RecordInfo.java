package com.devil.library.camera.params;

/**
 * 录制一段视频/音频的信息
 */
public class RecordInfo {

    private String fileName;

    private long duration; // 暂时录音的时长信息为-1，以视频信息为准

    private MediaType type;

    public RecordInfo(String fileName, long duration, MediaType type) {
        this.fileName = fileName;
        this.duration = duration;
        this.type = type;
    }

    public RecordInfo(String fileName, MediaType type) {
        this.fileName = fileName;
        this.duration = -1;
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public long getDuration() {
        return duration;
    }

    public MediaType getType() {
        return type;
    }
}
