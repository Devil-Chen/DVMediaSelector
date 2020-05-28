package com.devil.library.video.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.devil.library.media.utils.DisplayUtils;
import com.devil.library.media.utils.FileUtils;
import com.devil.library.video.VideoMediaManager;
import com.devil.library.video.listener.OnGetVideoFrameListener;
import com.devil.library.video.listener.SingleCallback;

import java.text.SimpleDateFormat;
import java.util.Date;

import VideoHandle.EpEditor;
import VideoHandle.OnEditorListener;

public class VideoTrimUtil {
  //单例
  private static VideoTrimUtil instance;

  private static final String TAG = VideoTrimUtil.class.getSimpleName();
  public static final long MIN_SHOOT_DURATION = 3000L;// 最小剪辑时间3s
  public static final int MIN_COUNT_RANGE = 10;  //seekBar的区域内一共有多少张图片


  public  int recyclerViewPadding  = 0;
  public  int videoFrameWidth = 0;
  private  int thumbWidth = 0;
  private  int thumbHeight = 0;

  /**
   * 获取单例
   * @return
   */
  public static VideoTrimUtil getInstance(Context mContext) {
    if (instance == null) {
      synchronized (VideoTrimUtil.class) {
        if (instance == null) {
          instance = new VideoTrimUtil();
          instance.init(mContext);
        }
      }
    }
    return instance;
  }

  private void init(Context mContext){
    int screenWidth = ScreenUtils.getScreenWidth(mContext);
//    recyclerViewPadding = DisplayUtils.dip2px(mContext,35);
    recyclerViewPadding = DisplayUtils.dip2px(mContext,15);
    videoFrameWidth = screenWidth - recyclerViewPadding * 2;
    thumbWidth = DisplayUtils.dip2px(mContext,35);
    thumbHeight = DisplayUtils.dip2px(mContext,50);
  }

  public void shootVideoThumbInBackground(final Context context, final String videoPath, final int totalThumbsCount, final long startPosition,
                                                 final long endPosition, final SingleCallback<Bitmap, Long> callback) {
    ThreadUtils.executeByCached(new Runnable() {
      @Override
      public void run() {
        //判断SD状态
        String state=Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
          //SD卡可用 使用FFmpeg获取缩略图
          long interval = (endPosition - startPosition) / totalThumbsCount;
          long[] frameTimeArray = new long[totalThumbsCount];
          for (int i = 0; i < totalThumbsCount; i++) {
            long frameTime = startPosition + interval * i;
            frameTimeArray[i] = frameTime;
          }
          getVideoThumb(context,videoPath,frameTimeArray,callback);
        }else{
          //SD卡不可用 使用默认获取缩略图
          try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(videoPath);
            // Retrieve media data use microsecond
            long interval = (endPosition - startPosition) / totalThumbsCount;
            for (int i = 0; i < totalThumbsCount; i++) {
              long frameTime = startPosition + interval * i;
              Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(frameTime * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
              if(bitmap == null) continue;
              try {
                bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbHeight, false);
              } catch (final Throwable t) {
                t.printStackTrace();
              }
              callback.onSingleCallback(bitmap, frameTime);
            }
            mediaMetadataRetriever.release();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }

      }
    });


  }

  private int currentPosition;

  /**
   * 使用ffmepg截取缩略图
   * @param videoPath
   * @param frameTime
   * @param callback
   */
  private void getVideoThumb(Context mContext,String videoPath,long[] frameTime, final SingleCallback<Bitmap, Long> callback){
    String saveDir = mContext.getExternalCacheDir().getAbsolutePath() + "/VideoThumb/";
//    String savePath = saveDir  + System.currentTimeMillis() + ".jpg";
    FileUtils.createDir(saveDir);
    VideoMediaManager.getVideoFrameArray(videoPath, saveDir, frameTime, new OnGetVideoFrameListener() {
      @Override
      public void onSuccess(String path, int currentPosition, boolean isFinish) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if(bitmap != null) {
          try {
            bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbHeight, false);
          } catch (final Throwable t) {
            t.printStackTrace();
          }
          callback.onSingleCallback(bitmap, frameTime[currentPosition]);
        }
      }

      @Override
      public void onFailure() {

      }
    });
//    EpEditor.videoFrame(videoPath, savePath, frameTime[currentPosition], new OnEditorListener() {
//      @Override
//      public void onSuccess() {
//        Bitmap bitmap = BitmapFactory.decodeFile(savePath);
//        if(bitmap != null) {
//          try {
//            bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbHeight, false);
//          } catch (final Throwable t) {
//            t.printStackTrace();
//          }
//          callback.onSingleCallback(bitmap, frameTime[currentPosition]);
//        }
//        //去到下一个位置
//        currentPosition += 1;
//        if (currentPosition < frameTime.length){
//          getVideoThumb(mContext,videoPath,frameTime,callback);
//        }else{
//          currentPosition = 0;
//        }
//      }
//
//      @Override
//      public void onFailure() {
//        currentPosition = 0;
//      }
//
//      @Override
//      public void onProgress(float v) {
//
//      }
//    });
  }

  public static String getVideoFilePath(String url) {
    if (TextUtils.isEmpty(url) || url.length() < 5) return "";
    if (url.substring(0, 4).equalsIgnoreCase("http")) {

    } else {
      url = "file://" + url;
    }

    return url;
  }

  private static String convertSecondsToTime(long seconds) {
    String timeStr = null;
    int hour = 0;
    int minute = 0;
    int second = 0;
    if (seconds <= 0) {
      return "00:00";
    } else {
      minute = (int) seconds / 60;
      if (minute < 60) {
        second = (int) seconds % 60;
        timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
      } else {
        hour = minute / 60;
        if (hour > 99) return "99:59:59";
        minute = minute % 60;
        second = (int) (seconds - hour * 3600 - minute * 60);
        timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
      }
    }
    return timeStr;
  }

  private static String unitFormat(int i) {
    String retStr = null;
    if (i >= 0 && i < 10) {
      retStr = "0" + Integer.toString(i);
    } else {
      retStr = "" + i;
    }
    return retStr;
  }
}
