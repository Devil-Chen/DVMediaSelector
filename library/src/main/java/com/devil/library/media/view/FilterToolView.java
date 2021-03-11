package com.devil.library.media.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.cgfay.filter.glfilter.resource.FilterHelper;
import com.cgfay.filter.glfilter.resource.bean.ResourceData;
import com.devil.library.camera.adapter.FilterAdapter;
import com.devil.library.media.R;
import com.devil.library.media.enumtype.ImageFilterType;
import com.devil.library.media.listener.OnAdjustChangeListener;
import com.devil.library.media.listener.OnFilterActionListener;

/**
 * 滤镜工具view
 */
public class FilterToolView extends LinearLayout implements View.OnClickListener,RadioGroup.OnCheckedChangeListener,OnAdjustChangeListener {
    //内容view
    private View mContentView;
    //上下文
    private Context mContext;
    //滤镜
    private RecyclerView rv_content;
    private FilterAdapter mFilterAdapter;
    private ImageView image_closeFilter;
    //调节
    private FilterAdjustView view_adjust;

    //头部内容
    private HorizontalScrollView sv_topContent;
    //头部选项按钮
    private RadioGroup rg_title;

    //回调
    private OnFilterActionListener filterListener;

    public FilterToolView(Context context) {
        super(context);
        //初始化
        init();
    }

    public FilterToolView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //初始化
        init();
    }

    public FilterToolView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FilterToolView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        //初始化
        init();
    }

    /**
     * 设置回调监听
     * @param listener
     */
    public void setOnFilterActionListener(OnFilterActionListener listener){
        this.filterListener = listener;
    }

    /**
     * 初始化
     */
    private void init(){
        mContext = getContext();
        mContentView = LayoutInflater.from(getContext()).inflate(R.layout.view_dv_filter_tool_layout,this);
        sv_topContent = mContentView.findViewById(R.id.sv_topContent);
        rg_title = mContentView.findViewById(R.id.rg_title);
        rg_title.setOnCheckedChangeListener(this);

        view_adjust = mContentView.findViewById(R.id.view_adjust);
        view_adjust.setOnAdjustChangeListener(this);

        initColorFilter();
    }

    /**
     * 只显示滤镜选项
     */
    public void setOnlyShowFilter(){
        if (sv_topContent.getVisibility() == View.VISIBLE){
            sv_topContent.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化颜色滤镜
     */
    private void initColorFilter(){
        rv_content = (RecyclerView) mContentView.findViewById(R.id.rv_content);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_content.setLayoutManager(linearLayoutManager);
        //初始化滤镜
        if (FilterHelper.getFilterList() == null || FilterHelper.getFilterList().size() == 0){
            FilterHelper.initAssetsFilter(mContext);
        }
        mFilterAdapter = new FilterAdapter(getContext(), FilterHelper.getFilterList());
        rv_content.setAdapter(mFilterAdapter);
        mFilterAdapter.setOnFilterChangeListener(onFilterChangeListener);

        image_closeFilter = mContentView.findViewById(R.id.image_closeFilter);
        image_closeFilter.setOnClickListener(this);
    }

    //滤镜转换
    private FilterAdapter.onFilterChangeListener onFilterChangeListener = new FilterAdapter.onFilterChangeListener(){

        @Override
        public void onFilterChanged(ResourceData resourceData) {
            if (mContext == null) {
                return;
            }
            if (filterListener != null){
                filterListener.onColorFilterChanged(resourceData);
            }
//            if (!resourceData.name.equals("none")) {
//
//            } else {
//
//            }
        }
    };

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.image_closeFilter){//关闭选择滤镜
            if (filterListener != null){
                filterListener.onCloseFilter();
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rb_colorFilter){//滤镜
            if (rv_content.getVisibility() == View.GONE){
                rv_content.setVisibility(View.VISIBLE);
            }
            if (view_adjust.getVisibility() == View.VISIBLE){
                view_adjust.setVisibility(View.GONE);
            }
        }else if(checkedId == R.id.rb_adjust){//调节
            if (rv_content.getVisibility() == View.VISIBLE){
                rv_content.setVisibility(View.GONE);
            }
            if (view_adjust.getVisibility() == View.GONE){
                view_adjust.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onAdjustChange(ImageFilterType type, float value) {
        if (filterListener != null){
            filterListener.onAdjustChange(type,value);
        }
    }
}
