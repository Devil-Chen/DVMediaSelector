package com.devil.library.media.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.devil.library.media.MediaSelectorManager;
import com.devil.library.media.R;
import com.devil.library.media.bean.MediaInfo;
import com.devil.library.media.ui.activity.DVEasyVideoPlayActivity;
import com.devil.library.media.utils.MediaFileTypeUtils;
import com.github.chrisbanes.photoview.OnOutsidePhotoTapListener;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * 默认资源浏览Fragment（每个Fragment显示一个）（放大图片/打开播放视频）
 */
public class WatchMediaVPItemFragment extends Fragment implements View.OnClickListener{
    //上下问
    private Activity mContext;

    /**显示内容rootView*/
    private View mContentView;
    //图片显示
    private PhotoView iv_photo;
    //播放按钮
    private ImageView iv_videoPlayIcon;

    //显示图片的地址
    private MediaInfo mediaInfo;

    /**
     * 创建实例
     * @param info
     * @return
     */
    public static WatchMediaVPItemFragment newInstance(MediaInfo info){
        WatchMediaVPItemFragment fragment = new WatchMediaVPItemFragment();
        final Bundle args = new Bundle();
        args.putSerializable("mediaInfo", info);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_dv_gv_item_watch_media,null);
        mContext = getActivity();
        //初始化View
        initView();
        //初始化数据
        initData();
        return mContentView;
    }

    /**
     * 初始化View
     */
    private void initView(){
        iv_photo = findViewById(R.id.iv_photo);
        iv_videoPlayIcon = findViewById(R.id.iv_videoPlayIcon);

        iv_videoPlayIcon.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData(){
        mediaInfo = (MediaInfo) getArguments().getSerializable("mediaInfo");

        iv_photo.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                mContext.onBackPressed();
            }
        });
        iv_photo.setOnOutsidePhotoTapListener(new OnOutsidePhotoTapListener() {
            @Override
            public void onOutsidePhotoTap(ImageView imageView) {
                mContext.onBackPressed();
            }
        });

        MediaSelectorManager.getInstance().displayImage(mContext,mediaInfo.filePath,iv_photo);
        //判断文件类型
        if (MediaFileTypeUtils.isVideoFileType(mediaInfo.filePath)){
            if (iv_videoPlayIcon.getVisibility() == View.GONE) iv_videoPlayIcon.setVisibility(View.VISIBLE);
        }else{
            if (iv_videoPlayIcon.getVisibility() == View.VISIBLE) iv_videoPlayIcon.setVisibility(View.GONE);
        }
    }

    /**
     * 找到控件
     * @param id
     * @param <T>
     * @return
     */
    public final <T extends View> T findViewById(@IdRes int id) {
        if(mContentView != null){
            return mContentView.findViewById(id);
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_videoPlayIcon){
            DVEasyVideoPlayActivity.openVideo(mContext,mediaInfo.filePath);
        }
    }
}
