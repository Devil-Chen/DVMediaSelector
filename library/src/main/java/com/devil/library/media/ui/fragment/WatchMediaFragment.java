package com.devil.library.media.ui.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devil.library.media.MediaSelectorManager;
import com.devil.library.media.R;
import com.devil.library.media.adapter.WatchMediaVPAdapter;
import com.devil.library.media.bean.MediaInfo;
import com.devil.library.media.listener.OnItemClickListener;
import com.devil.library.media.config.DVListConfig;
import com.devil.library.media.ui.activity.DVMediaSelectActivity;
import com.devil.library.media.view.HackyViewPager;

import java.util.List;

/**
 * 查看资源详情（放大图片/播放视频）
 */
public class WatchMediaFragment extends Fragment implements View.OnClickListener{
    //上下文
    public FragmentActivity mContext;
    //显示的视图
    public View mContentView;
    //配置
    private DVListConfig config;
    //显示图片的viewpager
    private HackyViewPager vp_content;
    //显示数量
    private TextView tv_pageTip;
    //是否已经选择布局
    private LinearLayout line_checkBox;
    //显示是否选中状态
    private ImageView iv_check;
    //菜单点击监听者
    private OnItemClickListener onItemClickListener;

    //内容
    private List<MediaInfo> li_mediaInfo;


    /**
     * 获取实例
     * @return
     */
    public static WatchMediaFragment instance() {
        return new WatchMediaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (this.mContext == null){
            this.mContext = getActivity();
        }
        if (config == null){
            config = MediaSelectorManager.getInstance().getCurrentListConfig();
        }
        if (mContentView == null){
            mContentView = inflater.inflate(R.layout.fragment_dv_watch_media,null);

        }
        //初始化view
        initView();
        //初始化数据
        initData();
        return mContentView;
    }
    /**
     * 根据id查找view
     * @param id
     * @param <T>
     * @return
     */
    public <T extends View> T findViewById(@IdRes int id) {
        return mContentView.findViewById(id);
    }

    /**
     * 设置菜单点击事件监听者
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 初始化view
     */
    private void initView(){
        vp_content = findViewById(R.id.vp_content);
        tv_pageTip = findViewById(R.id.tv_pageTip);
        line_checkBox = findViewById(R.id.line_checkBox);
        iv_check = findViewById(R.id.iv_check);

        line_checkBox.setOnClickListener(this);
        vp_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

                tv_pageTip.setText((i + 1)+"/"+li_mediaInfo.size());
                //设置是否选中
                if (DVMediaSelectActivity.map_cacheSelectInfo.containsKey(li_mediaInfo.get(i).filePath)){
                    int checkIcon = config.checkIconResource != 0 ? config.checkIconResource : R.mipmap.icon_dv_checked;
                    iv_check.setImageResource(checkIcon);
                }else{
                    int unCheckIcon = config.unCheckIconResource != 0 ? config.unCheckIconResource : R.mipmap.icon_dv_unchecked;
                    iv_check.setImageResource(unCheckIcon);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData(){
        li_mediaInfo = (List<MediaInfo>) getArguments().getSerializable("mediaInfos");
        int firstPosition = getArguments().getInt("firstPosition",0);
        vp_content.setAdapter(new WatchMediaVPAdapter(mContext,getChildFragmentManager(),li_mediaInfo));

        tv_pageTip.setText((firstPosition+1)+"/"+li_mediaInfo.size());

        vp_content.setCurrentItem(firstPosition);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.line_checkBox){
            //设置监听者
            if (onItemClickListener != null){
                MediaInfo info = li_mediaInfo.get(vp_content.getCurrentItem());
                boolean isChecked = !DVMediaSelectActivity.map_cacheSelectInfo.containsKey(info.filePath);
                int position = vp_content.getCurrentItem();
                //判断是否可选择
                if (onItemClickListener.itemCheckEnabled(position,isChecked)){
                    onItemClickListener.onItemCheck(info,isChecked);
                    if (isChecked){
                        int checkIcon = config.checkIconResource != 0 ? config.checkIconResource : R.mipmap.icon_dv_checked;
                        iv_check.setImageResource(checkIcon);
                    }else{
                        int unCheckIcon = config.unCheckIconResource != 0 ? config.unCheckIconResource : R.mipmap.icon_dv_unchecked;
                        iv_check.setImageResource(unCheckIcon);
                    }

                }
            }
        }
    }
}
