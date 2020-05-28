package com.devil.library.camera.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cgfay.filter.glfilter.resource.bean.ResourceData;
import com.cgfay.uitls.utils.BitmapUtils;
import com.devil.library.media.R;


import java.util.List;

/**
 * Created by why8222 on 2016/3/17.
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder>{
    
    private List<ResourceData> mFilterDataList;
    private Context mContext;
    private int selected = 0;

    public FilterAdapter(Context context,List<ResourceData> resourceDataList) {
        this.mFilterDataList = resourceDataList;
        this.mContext = context;
    }

    @Override
    public FilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_dv_rv_filter,
                parent, false);
        FilterHolder viewHolder = new FilterHolder(view);
        viewHolder.thumbImage = (ImageView) view
                .findViewById(R.id.filter_thumb_image);
        viewHolder.filterName = (TextView) view
                .findViewById(R.id.filter_thumb_name);
        viewHolder.filterRoot = (FrameLayout)view
                .findViewById(R.id.filter_root);
        viewHolder.thumbSelected = (FrameLayout) view
                .findViewById(R.id.filter_thumb_selected);
        viewHolder.thumbSelected_bg = view.
                findViewById(R.id.filter_thumb_selected_bg);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FilterHolder holder,final int position) {
        if (mFilterDataList.get(position).thumbPath.startsWith("assets://")) {
            holder.thumbImage.setImageBitmap(BitmapUtils.getImageFromAssetsFile(mContext,
                    mFilterDataList.get(position).thumbPath.substring("assets://".length())));
        } else {
            holder.thumbImage.setImageBitmap(BitmapUtils.getBitmapFromFile(mFilterDataList.get(position).thumbPath));
        }
        holder.filterName.setText(mFilterDataList.get(position).name);
        holder.filterName.setBackgroundColor(mContext.getResources().getColor(
                FilterTypeHelper.FilterType2Color(mFilterDataList.get(position).name)));
        if(position == selected){
            holder.thumbSelected.setVisibility(View.VISIBLE);
            holder.thumbSelected_bg.setBackgroundColor(mContext.getResources().getColor(
                    FilterTypeHelper.FilterType2Color(mFilterDataList.get(position).name)));
            holder.thumbSelected_bg.setAlpha(0.7f);
        }else {
            holder.thumbSelected.setVisibility(View.GONE);
        }

        holder.filterRoot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(selected == position)
                    return;
                int lastSelected = selected;
                selected = position;
                notifyItemChanged(lastSelected);
                notifyItemChanged(position);
                onFilterChangeListener.onFilterChanged(mFilterDataList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilterDataList == null ? 0 : mFilterDataList.size();
    }

    class FilterHolder extends RecyclerView.ViewHolder {
        ImageView thumbImage;
        TextView filterName;
        FrameLayout thumbSelected;
        FrameLayout filterRoot;
        View thumbSelected_bg;

        public FilterHolder(View itemView) {
            super(itemView);
        }
    }

    public interface onFilterChangeListener{
        void onFilterChanged(ResourceData resourceData);
    }

    private onFilterChangeListener onFilterChangeListener;

    public void setOnFilterChangeListener(onFilterChangeListener onFilterChangeListener){
        this.onFilterChangeListener = onFilterChangeListener;
    }
}
