package com.devil.library.media.base;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.SparseArray;

/**
 * ViewPager基适配器
 */
public abstract class BaseVPFragmentAdapter extends FragmentPagerAdapter {
    /**上下文*/
    protected Context mContext;
    /**
     * 缓存fragment
     */
    private SparseArray<Fragment> mapCacheFragment = new SparseArray<>();

    public BaseVPFragmentAdapter(Context mContext, FragmentManager fragmentManager){
        super(fragmentManager);
        this.mContext = mContext;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment cacheFragment = mapCacheFragment.get(i);
        if (cacheFragment != null){
            return cacheFragment;
        }else{
            cacheFragment = createFragment(i);
            mapCacheFragment.put(i,cacheFragment);
        }
        return cacheFragment;
    }

    @Override
    public int getItemPosition(Object object) {
        //这是ViewPager适配器的特点,有两个值 POSITION_NONE，POSITION_UNCHANGED，默认就是POSITION_UNCHANGED,
        // 表示数据没变化不用更新.notifyDataChange的时候重新调用getViewForPage
        return PagerAdapter.POSITION_UNCHANGED;
    }

    /**
     * 通过position获取fragment
     * @param position
     * @return
     */
    public Fragment getFragmentWithPosition(int position){
        return mapCacheFragment.get(position);
    }

    /**
     * 创建fragment
     * @param position
     * @return 布局Id
     */
    public abstract Fragment createFragment(int position);


}
