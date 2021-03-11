package com.devil.library.media.base;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * RecyclerView的基类adapter
 */
public abstract class BaseRVAdapter<H extends EasyRVHolder,P extends Object> extends RecyclerView.Adapter<H>{
    //上下文
    public Context mContext;
    // 外面传入的数据
    public List<P> li_content;
    /**子菜单点击事件监听*/
    protected OnItemClickListener itemClickListener;

    public BaseRVAdapter(Context mContext, List<P> li_content) {
        this.mContext = mContext;
        this.li_content = li_content;
    }

    /**
     * 设置子菜单点击事件监听
     * @param itemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    /**
     * 获取总的条目数量（设置多个viewType，需重写此方法）
     */
    @Override

    public int getItemCount() {
        // TODO Auto-generated method stub
        return li_content != null ? li_content.size()  : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public H onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        // TODO Auto-generated method stub
        View itemView = LayoutInflater.from(mContext).inflate(getViewLayoutId(viewType), parent, false);
        final H holder = createHolder(itemView,viewType);
        if (itemClickListener != null){
            holder.setOnItemViewClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(holder,holder.getLayoutPosition());
                }
            });
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final H holder, final int position) {
        initData(holder,position);
    }

    /**
     * 布局的layout id
     * @return
     */
    protected abstract int getViewLayoutId(int viewType);

    /**
     * 创建holder
     * @return
     */
    protected abstract H createHolder(View itemView,int viewType);

    /**
     * 初始化数据
     */
    protected abstract void initData(H holder, int position);

    /**
     * 子菜单点击监听
     */
    public interface OnItemClickListener{
       void onItemClick( EasyRVHolder holder,int position);
    }
}
