package com.devil.library.media.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.devil.library.media.R;
import com.devil.library.media.bean.FolderInfo;

import java.util.List;

/**
 * 文件夹列表适配器
 */
public class FolderLVAdapter extends BaseAdapter {
    //上下文
    private Context mContext;
    //内容
    private List<FolderInfo> li_content;
    //当前选择的文件夹
    private int selectIndex = 0;

    public FolderLVAdapter(Context mContext,List<FolderInfo> li_content){
        this.mContext = mContext;
        this.li_content = li_content;
    }

    @Override
    public int getCount() {
        return li_content == null ? 0 : li_content.size();
    }

    @Override
    public Object getItem(int i) {
        return li_content.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_dv_lv_folder,null);
            holder = new ViewHolder();
            holder.iv_folder = view.findViewById(R.id.iv_folder);
            holder.iv_check = view.findViewById(R.id.iv_check);
            holder.tv_folderName = view.findViewById(R.id.tv_folderName);
            holder.tv_count = view.findViewById(R.id.tv_count);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        FolderInfo info = li_content.get(i);
        //设置文件名称
        holder.tv_folderName.setText(""+info.folderName);
        //设置文件数量
        holder.tv_count.setText(""+info.fileCount);
        //设置选中状态
        if (i == selectIndex){
            holder.iv_check.setVisibility(View.VISIBLE);
        }else{
            holder.iv_check.setVisibility(View.GONE);
        }
        return view;
    }

    /**
     * 设置选择文件夹
     * @param selectIndex
     */
    public void setSelectIndex(int selectIndex){
        this.selectIndex = selectIndex;
        notifyDataSetChanged();
    }

    public class ViewHolder{
        public ImageView iv_folder;
        public ImageView iv_check;
        public TextView tv_folderName;
        public TextView tv_count;
    }
}
