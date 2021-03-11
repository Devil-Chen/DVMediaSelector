package com.devil.library.camera.params;

import android.content.Context;
import android.media.AudioFormat;
import android.os.Environment;
import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;

/**
 * 音频参数
 */
public class AudioParams {

    public static final String MIME_TYPE = "audio/mp4a-latm";

    public static final int SAMPLE_RATE = 44100;        // 44.1[KHz] is only setting guaranteed to be available on all devices.

    //    public static final int BIT_RATE = 96000;
    // 与抖音相同的音频比特率
    public static final int BIT_RATE = 128000;

    private int mSampleRate;    // 采样率
    private int mChannel;       // 采样声道
    private int mBitRate;       // 比特率
    private int mAudioFormat;   // 采样格式


    private String mAudioPath;  // 文件名
    private long mMaxDuration;  // 最大时长

    public AudioParams() {
        mSampleRate = SAMPLE_RATE;
        mChannel =  AudioFormat.CHANNEL_IN_STEREO;
        mBitRate = BIT_RATE;
        mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    }

    public void setSampleRate(int sampleRate) {
        this.mSampleRate = sampleRate;
    }

    public int getSampleRate() {
        return mSampleRate;
    }

    public void setChannel(int channel) {
        mChannel = channel;
    }

    public int getChannel() {
        return mChannel;
    }

    public void setBitRate(int bitRate) {
        mBitRate = bitRate;
    }

    public int getBitRate() {
        return mBitRate;
    }

    public void setAudioFormat(int audioFormat) {
        mAudioFormat = audioFormat;
    }

    public int getAudioFormat() {
        return mAudioFormat;
    }


    public void setAudioPath(String audioPath) {
        this.mAudioPath = audioPath;
    }

    public String getAudioPath() {
        return mAudioPath;
    }

    public void setMaxDuration(long maxDuration) {
        this.mMaxDuration = maxDuration;
    }

    public long getMaxDuration() {
        return mMaxDuration;
    }


    /**
     * 获取音频缓存绝对路径
     * @param context
     * @return
     */
    public static String getAudioTempPath(@NonNull Context context) {
        String directoryPath;
        // 判断外部存储是否可用，如果不可用则使用内部存储路径
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            directoryPath = context.getExternalCacheDir().getAbsolutePath();
        } else { // 使用内部存储缓存目录
            directoryPath = context.getCacheDir().getAbsolutePath();
        }
        String path = directoryPath + File.separator + "temp.aac";
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}
