package com.devil.library.camera.params;
/**
 * 媒体信息
 */
public class MediaInfo {

    private String fileName;
    private long duration;

    public MediaInfo(String name, long duration) {
        this.fileName = name;
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public String getFileName() {
        return fileName;
    }
}
