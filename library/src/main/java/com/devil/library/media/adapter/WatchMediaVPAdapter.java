package com.devil.library.media.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import com.devil.library.media.base.BaseVPFragmentAdapter;
import com.devil.library.media.bean.MediaInfo;
import com.devil.library.media.ui.fragment.WatchMediaVPItemFragment;

import java.util.List;

/**
 * 资源浏览ViewPager适配器
 */
public class WatchMediaVPAdapter extends BaseVPFragmentAdapter {
    private List<MediaInfo> li_mediaInfo;
    public WatchMediaVPAdapter(Context mContext, FragmentManager fragmentManager, List<MediaInfo> li_mediaInfo){
        super(mContext,fragmentManager);
        this.li_mediaInfo = li_mediaInfo;
    }
    @Override
    public int getCount() {
        return li_mediaInfo != null ? li_mediaInfo.size() : 0;
    }

    @Override
    public Fragment createFragment(int position) {
        return WatchMediaVPItemFragment.newInstance(li_mediaInfo.get(position));
    }


}