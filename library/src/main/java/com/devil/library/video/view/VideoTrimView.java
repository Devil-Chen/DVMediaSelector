package com.devil.library.video.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devil.library.media.R;
import com.devil.library.media.utils.DisplayUtils;
import com.devil.library.video.adapter.VideoTrimAdapter;
import com.devil.library.video.listener.OnPlayerEventListener;
import com.devil.library.video.listener.SingleCallback;
import com.devil.library.video.listener.OnSelectVideoTrimListener;
import com.devil.library.video.utils.DVLinearLayoutManager;
import com.devil.library.video.utils.ScreenUtils;
import com.devil.library.video.utils.VideoTrimUtil;

/**
 * 视频剪辑view
 */
public class VideoTrimView extends FrameLayout {

  private static final String TAG = "VideoTrim";
  //最大裁剪时间（不设置则为视频时长）
  private long maxShootDuration = 0;
  //最小裁剪时间（默认3秒）
  private long minShootDuration = VideoTrimUtil.MIN_SHOOT_DURATION;
  private int mMaxWidth = 0;
  private Context mContext;
  private RelativeLayout mLinearVideo;
  private DVVideoView mVideoView;
  private ImageView mPlayView;
  private RecyclerView mVideoThumbRecyclerView;
  private DVLinearLayoutManager layoutManager;
  private RangeSeekBarView mRangeSeekBarView;
  private LinearLayout mSeekBarLayout;

  private TextView mVideoShootTipTv;
  private float mAverageMsPx;//每毫秒所占的px
  private float averagePxMs;//每px所占用的ms毫秒
  private String mSourcePath;//视频地址
  private OnSelectVideoTrimListener mOnTrimVideoListener;
  private long mDuration = 0;
  private VideoTrimAdapter mVideoThumbAdapter;
  private boolean isFromRestore = false;
  //new
  private long mLeftProgressPos, mRightProgressPos;
  private long mRedProgressBarPos = 0;
  private long scrollPos = 0;
  private int mScaledTouchSlop;
  private int lastScrollX;
  private boolean isOverScaledTouchSlop;
  private int mThumbsTotalCount;
  //进度显示
  private SeekBar sb_playProgress;

  public VideoTrimView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public VideoTrimView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  /**
   * 初始化view
   * @param context
   */
  private void init(Context context) {
    this.mContext = context;
    LayoutInflater.from(context).inflate(R.layout.view_dv_video_trimmer, this, true);

    mMaxWidth = VideoTrimUtil.getInstance(mContext).videoFrameWidth;
    mLinearVideo = findViewById(R.id.layout_surface_view);
    mVideoView = findViewById(R.id.video_loader);
    mPlayView = findViewById(R.id.icon_video_play);
    mSeekBarLayout = findViewById(R.id.seekBarLayout);
    mVideoShootTipTv = findViewById(R.id.video_shoot_tip);
    mVideoThumbRecyclerView = findViewById(R.id.video_frames_recyclerView);
    sb_playProgress = findViewById(R.id.sb_playProgress);
    layoutManager = new DVLinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false );
    mVideoThumbRecyclerView.setLayoutManager(layoutManager);
    mVideoThumbAdapter = new VideoTrimAdapter(mContext);
    mVideoThumbRecyclerView.setAdapter(mVideoThumbAdapter);


    setUpListeners();
  }

  /**
   * 初始化显示进度的view
   */
  private void initRangeSeekBarView() {
    if(mRangeSeekBarView != null) return;
    int rangeWidth;
    mLeftProgressPos = 0;
    if (maxShootDuration == 0){
      maxShootDuration = mDuration;
      layoutManager.setCanScroll(false);
    }else{
        //截取固定长度时监听
        mVideoThumbRecyclerView.addOnScrollListener(mOnScrollListener);
    }
    //设置seekBar最大进度为最大截取时长
    sb_playProgress.setMax((int) maxShootDuration);
    sb_playProgress.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);//设置滑块颜色、样式
    //判断最大截取时长是否等于视频时长，等于则RV不可滑动
    if (mDuration <= maxShootDuration) {
      int thumbTotalWidth = ScreenUtils.getScreenWidth(mContext) - DisplayUtils.dip2px(mContext,100);
      mThumbsTotalCount = thumbTotalWidth / DisplayUtils.dip2px(mContext,35);
      if (mThumbsTotalCount < VideoTrimUtil.MIN_COUNT_RANGE ){
        mThumbsTotalCount = VideoTrimUtil.MIN_COUNT_RANGE;
      }
      rangeWidth = mMaxWidth;
      mRightProgressPos = mDuration;
    } else {
      mThumbsTotalCount = (int) (mDuration * 1.0f / (maxShootDuration * 1.0f) * VideoTrimUtil.MIN_COUNT_RANGE);
      rangeWidth = mMaxWidth / VideoTrimUtil.MIN_COUNT_RANGE * mThumbsTotalCount;
      mRightProgressPos = maxShootDuration;
    }
    mVideoThumbRecyclerView.addItemDecoration(new SpacesItemDecoration(VideoTrimUtil.getInstance(mContext).recyclerViewPadding, mThumbsTotalCount));
    //创建控制截取时长的view
    mRangeSeekBarView = new RangeSeekBarView(mContext, mLeftProgressPos, mRightProgressPos);
    mRangeSeekBarView.setSelectedMinValue(mLeftProgressPos);
    mRangeSeekBarView.setSelectedMaxValue(mRightProgressPos);
    mRangeSeekBarView.setStartEndTime(mLeftProgressPos, mRightProgressPos);
    mRangeSeekBarView.setMinShootTime(minShootDuration);
    mRangeSeekBarView.setNotifyWhileDragging(true);
    mRangeSeekBarView.setOnRangeSeekBarChangeListener(mOnRangeSeekBarChangeListener);
    mSeekBarLayout.addView(mRangeSeekBarView);

    mAverageMsPx = mDuration * 1.0f / rangeWidth * 1.0f;
    averagePxMs = (mMaxWidth * 1.0f / (mRightProgressPos - mLeftProgressPos));
  }

  /**
   * 设置最大截取时长
   * @param maxShootDuration 最大时长
   */
  public void setMaxShootDuration(int maxShootDuration){
    this.maxShootDuration = maxShootDuration;
  }

  /**
   * 设置最小截取时长
   * @param minShootDuration 最小时长（大于1）
   */
  public void setMinShootDuration(int minShootDuration){
    if (minShootDuration < 1){
      return;
    }
    this.minShootDuration = minShootDuration;
  }

  public void initVideoByPath(final String  videoPath) {
    mSourcePath = videoPath;
    mVideoView.setVideoPath(videoPath);
    mVideoView.requestFocus();
    mVideoShootTipTv.setText(String.format(mContext.getResources().getString(R.string.video_shoot_tip)));
  }

  /**
   * 开始截取视频图片
   */
  private void startShootVideoThumbs(final Context context, final String videoPath, int totalThumbsCount, long startPosition, long endPosition) {
    VideoTrimUtil.getInstance(mContext).shootVideoThumbInBackground(context, videoPath, totalThumbsCount, startPosition, endPosition,
        new SingleCallback<Bitmap, Long>() {
          @Override
          public void onSingleCallback(final Bitmap bitmap, final Long interval) {
            if (bitmap != null) {
              VideoTrimView.this.post(new Runnable() {
                @Override
                public void run() {
                  mVideoThumbAdapter.addBitmaps(bitmap);
                }
              });
            }
          }
        });
  }

  /**
   * 取消按钮点击
   */
  private void onCancelClicked() {
    if (mOnTrimVideoListener != null){
      mOnTrimVideoListener.onCancelSelect();
    }
  }

  private void videoPrepared() {
    mDuration = mVideoView.getDuration();
    if (!getRestoreState()) {
      seekTo((int) mRedProgressBarPos);
    } else {
      setRestoreState(false);
      seekTo((int) mRedProgressBarPos);
    }
    initRangeSeekBarView();
    startShootVideoThumbs(mContext, mSourcePath, mThumbsTotalCount, 0, mDuration);
  }

  private void videoCompleted() {
    seekTo(mLeftProgressPos);
    //确保视频暂停
    mVideoView.pause();
    setPlayPauseViewIcon(false);
  }

  private void onVideoReset() {
    mVideoView.pause();
    setPlayPauseViewIcon(false);
  }

  /**
   * 播放或停止视频
   */
  private void playVideoOrPause() {
    mRedProgressBarPos = mVideoView.getCurrentPosition();
    if (mVideoView.isPlaying()) {
      mVideoView.pause();
      pauseSeekBar();
      setPlayPauseViewIcon(false);
    } else {
      mVideoView.start();
      playingSeekBar();
      setPlayPauseViewIcon(true);
    }
  }

  public void onVideoPause() {
    if (mVideoView.isPlaying()) {
      seekTo(mLeftProgressPos);//复位
      mVideoView.pause();
      setPlayPauseViewIcon(false);
      sb_playProgress.setVisibility(GONE);
    }
  }

  public void setOnTrimVideoListener(OnSelectVideoTrimListener onTrimVideoListener) {
    mOnTrimVideoListener = onTrimVideoListener;
  }

  /**
   * 设置监听者
   */
  private void setUpListeners() {
    //取消
    findViewById(R.id.cancelBtn).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        onCancelClicked();
      }
    });
    //完成
    findViewById(R.id.finishBtn).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        onSaveClicked();
      }
    });
    //播放器事件
    mVideoView.setPlayerEventListener(new OnPlayerEventListener() {
      @Override
      public void onError() {
        //Log.e(TAG,"onError");
      }

      @Override
      public void onCompletion() {
        videoCompleted();
        //Log.e(TAG,"onCompletion");
      }

      @Override
      public void onInfo(int what, int extra) {
        //Log.e(TAG,"onInfo");
      }

      @Override
      public void onPrepared() {
        videoPrepared();
        if (!mVideoView.isPlaying()){
          playVideoOrPause();
        }else{
          setPlayPauseViewIcon(mVideoView.isPlaying());
          playingSeekBar();
        }
        //Log.e(TAG,"onPrepared");
      }

      @Override
      public void onVideoSizeChanged(int width, int height) {
        //Log.e(TAG,"onVideoSizeChanged");
      }
    });
    mPlayView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        playVideoOrPause();
      }
    });
  }

  /**
   * 完成选择点击
   */
  private void onSaveClicked() {
    if (mRightProgressPos - mLeftProgressPos < minShootDuration) {
      Toast.makeText(mContext, "视频长不足"+ (minShootDuration / 1000) +"秒", Toast.LENGTH_SHORT).show();
    } else {
      if (mVideoView.isPlaying()){
        playVideoOrPause();
      }
      if (mOnTrimVideoListener != null){
        mOnTrimVideoListener.onAlreadySelect(mSourcePath,mLeftProgressPos,mRightProgressPos,mDuration);
      }
    }
  }

  /**
   * 去到某个播放点
   * @param msec
   */
  private void seekTo(long msec) {
    mVideoView.seekTo(msec);
    //Log.e(TAG, "seekTo = " + msec);
  }

  private boolean getRestoreState() {
    return isFromRestore;
  }

  public void setRestoreState(boolean fromRestore) {
    isFromRestore = fromRestore;
  }

  /**
   * 设置播放按钮图片
   * @param isPlaying
   */
  private void setPlayPauseViewIcon(boolean isPlaying) {
    mPlayView.setImageResource(isPlaying ? R.mipmap.icon_dv_video_pause_black : R.mipmap.icon_dv_video_play_black);
  }

    /**
     * 更新起止位置信息
     * @param startValue 开始位置
     * @param endValue 结束位置
     */
  private void updateProgressPos(long startValue,long endValue){
      mLeftProgressPos = startValue + scrollPos;
      mRedProgressBarPos = mLeftProgressPos;
      mRightProgressPos = endValue + scrollPos;
      mRangeSeekBarView.setStartEndTime(mLeftProgressPos, mRightProgressPos);
  }
  private final RangeSeekBarView.OnRangeSeekBarChangeListener mOnRangeSeekBarChangeListener = new RangeSeekBarView.OnRangeSeekBarChangeListener() {
    //开始
    @Override
    public void onValuesChangeStart(RangeSeekBarView bar, long minValue, long maxValue, int action, boolean isMin, RangeSeekBarView.Thumb pressedThumb) {
      //停止播放视频
      if (mVideoView.isPlaying()) {
          playVideoOrPause();
      }
      //更新起止位置信息
        updateProgressPos(minValue,maxValue);
    }
    //改变中
    @Override
    public void onValuesChanged(RangeSeekBarView bar, long minValue, long maxValue, int action, boolean isMin,
                                            RangeSeekBarView.Thumb pressedThumb) {
      //Log.e(TAG, "-----minValue----->>>>>>" + minValue);
      //Log.e(TAG, "-----maxValue----->>>>>>" + maxValue);
        //更新起止位置信息
        updateProgressPos(minValue,maxValue);
      //Log.e(TAG, "-----mLeftProgressPos----->>>>>>" + mLeftProgressPos);
      //Log.e(TAG, "-----mRightProgressPos----->>>>>>" + mRightProgressPos);

    }

   //结束
    @Override
    public void onValuesChangeEnd(RangeSeekBarView bar, long minValue, long maxValue, int action, boolean isMin, RangeSeekBarView.Thumb pressedThumb) {
      //更新起止位置信息
      updateProgressPos(minValue,maxValue);
      seekTo((int) mLeftProgressPos);
      playVideoOrPause();
    }
  };

  private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
      super.onScrollStateChanged(recyclerView, newState);
      //Log.e(TAG, "newState = " + newState);
      if (newState == 0){//停止的时候
          seekTo(mLeftProgressPos);
          playVideoOrPause();
      }else{
          if (mVideoView.isPlaying()) {
              mVideoView.pause();
              setPlayPauseViewIcon(false);
          }
      }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      super.onScrolled(recyclerView, dx, dy);
      int scrollX = calcScrollXDistance();
      //达不到滑动的距离
      if (Math.abs(lastScrollX - scrollX) < mScaledTouchSlop) {
        isOverScaledTouchSlop = false;
        return;
      }
      isOverScaledTouchSlop = true;
      //初始状态,why ? 因为默认的时候有recyclerViewPadding dp的空白！
      if (scrollX == -VideoTrimUtil.getInstance(mContext).recyclerViewPadding) {
        scrollPos = 0;
      } else {
        scrollPos = (long) (mAverageMsPx * (VideoTrimUtil.getInstance(mContext).recyclerViewPadding + scrollX));
        mLeftProgressPos = mRangeSeekBarView.getSelectedMinValue() + scrollPos;
        mRightProgressPos = mRangeSeekBarView.getSelectedMaxValue() + scrollPos;
        //Log.e(TAG, "onScrolled >>>> mLeftProgressPos = " + mLeftProgressPos);
        mRedProgressBarPos = mLeftProgressPos;

        sb_playProgress.setVisibility(GONE);

        mRangeSeekBarView.setStartEndTime(mLeftProgressPos, mRightProgressPos);
        mRangeSeekBarView.invalidate();
      }
      lastScrollX = scrollX;
    }
  };

  /**
   * 水平滑动了多少px
   */
  private int calcScrollXDistance() {
    LinearLayoutManager layoutManager = (LinearLayoutManager) mVideoThumbRecyclerView.getLayoutManager();
    int position = layoutManager.findFirstVisibleItemPosition();
    View firstVisibleChildView = layoutManager.findViewByPosition(position);
    int itemWidth = firstVisibleChildView.getWidth();
    return (position) * itemWidth - firstVisibleChildView.getLeft();
  }

  /**
   * 准备开始设置SeekBar进度
   */
  private void playingSeekBar() {
    pauseSeekBar();
    //显示进度
    if (sb_playProgress.getVisibility() == View.GONE) {
      sb_playProgress.setVisibility(View.VISIBLE);
    }
    post(updateSeekBarRunnable);
  }

  /**
   * 停止seekBar进度设置
   */
  private void pauseSeekBar() {
      removeCallbacks(updateSeekBarRunnable);
  }

  /**
   * 更新seekBar进度的Runnable
   */
  private Runnable updateSeekBarRunnable = new Runnable() {

    @Override
    public void run() {
      updateVideoProgress();
    }
  };

  /**
   * 更新seekBar进度实际方法
   */
  private void updateVideoProgress() {
    long currentPosition = mVideoView.getCurrentPosition();
    //设置进度位置
    sb_playProgress.setProgress((int) currentPosition);
//    //Log.e(TAG, "updateVideoProgress currentPosition = " + currentPosition);

    if (currentPosition >= (mRightProgressPos)) {//播放完成
      mRedProgressBarPos = mLeftProgressPos;
      pauseSeekBar();
      onVideoPause();
    } else {//没播放完，继续刷新（50毫秒一次）
        postDelayed(updateSeekBarRunnable,50);
    }
  }



  /**
   * 释放播放器
   */
  public void release() {
      removeCallbacks(updateSeekBarRunnable);
      mVideoView.release();
  }
}
