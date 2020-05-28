package com.devil.library.video.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频截图时使用的信息
 */
public class VideoCropFrameInfo {
    //视频地址
    public String videoPath;
    //保存地址
    public String savePath;
    //截取的时间点
    public long cropFrameTime;


    public static List<VideoCropFrameInfo> timeArray2Info(String videoPath, String saveDir, long[] frameTimeArray) {
        List<VideoCropFrameInfo> list = new ArrayList<>();
        for (int i = 0 ; i < frameTimeArray.length ; i ++) {
            long time = frameTimeArray[i];
            String savePath = saveDir + "/" + System.currentTimeMillis() + i + ".jpg";
            VideoCropFrameInfo info = new VideoCropFrameInfo();
            info.videoPath = videoPath;
            info.savePath = savePath;
            info.cropFrameTime = time;
            list.add(info);
        }
        return list;
    }
}
