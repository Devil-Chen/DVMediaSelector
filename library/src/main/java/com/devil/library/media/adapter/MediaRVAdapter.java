package com.devil.library.media.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.devil.library.media.MediaSelectorManager;
import com.devil.library.media.R;
import com.devil.library.media.base.BaseRVAdapter;
import com.devil.library.media.base.EasyRVHolder;
import com.devil.library.media.bean.MediaInfo;
import com.devil.library.media.config.DVListConfig;
import com.devil.library.media.ui.activity.DVMediaSelectActivity;
import com.devil.library.media.utils.MediaFileTypeUtils;

import java.util.List;

/**
 * 媒体数据Adapter
 */
public class MediaRVAdapter extends BaseRVAdapter<EasyRVHolder,MediaInfo> {
    //资源选择监听
    private OnItemCheckListener listener;
    //配置
    private DVListConfig config;

    public MediaRVAdapter(Context mContext, List<MediaInfo> li_content) {
        super(mContext, li_content);
        config = MediaSelectorManager.getInstance().getCurrentListConfig();
    }

    /**
     * 设置资源选择监听
     * @param listener
     */
    public void setOnItemCheckListener(OnItemCheckListener listener){
        this.listener = listener;
    }
    /**
     * 获取总的条目数量
     */
    @Override

    public int getItemCount() {
        // TODO Auto-generated method stub
        int size = li_content != null ? li_content.size()  : 0;
        if (needCamera()){
            size += 1;
        }
        return size;
    }
    @Override
    protected int getViewLayoutId(int viewType) {
        if (viewType == 0){
            return R.layout.item_dv_rv_first_take_photo;
        }else{
            return R.layout.item_dv_rv_media_list;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && needCamera()){//只有单选能加camera
            return 0;
        }else{
            return 1;
        }
    }

    /**
     * 是否需要添加camera
     * @return
     */
    private boolean needCamera(){
        return config.needCamera ; //&& !config.multiSelect;
    }

    /**
     * 是否需要自动设置子菜单点击事件监听（默认true）
     * @return 需要设置/不需要设置
     */
    protected boolean needAutoSetUpItemClick(){
        return false;
    }

    @Override
    protected EasyRVHolder createHolder(View itemView, int position) {
        return new EasyRVHolder(mContext,itemView);
    }

    @Override
    protected void initData(final EasyRVHolder holder, final  int position) {
        if (itemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        int realPosition = position;
                        if (position == 0 && needCamera()){
                            realPosition = -1;
                        }else{
                            if (needCamera()){
                                realPosition -= 1;
                            }
                        }
                        itemClickListener.onItemClick(holder,realPosition);
                    }
                }
            });
        }
        //判断是否有照相机
        if (position == 0) {
            if (!needCamera()){//如果是不需要照相机的话，直接设置数据
                //设置内容数据
                setUpContentData(holder, position);
            }else{//设置照相机图标
                if (config.cameraIconResource != 0){
                    holder.setImageResource(R.id.iv_takePhoto,config.cameraIconResource);
                }
            }
        }else{
            //计算真实位置
            int realPosition = position;
            if (needCamera()){
                realPosition -= 1;
            }
            //设置内容数据
            setUpContentData(holder, realPosition);
        }
    }

    /**
     * 设置内容数据
     * @param holder
     * @param position
     */
    private void setUpContentData(final EasyRVHolder holder, final int position){
        //信息实体
        final MediaInfo info = li_content.get(position);
        //设置checkbox状态改变监听
        holder.setOnClickListener(R.id.line_checkBox, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    ImageView iv_check = holder.getView(R.id.iv_check);
                    boolean isChecked = !DVMediaSelectActivity.map_cacheSelectInfo.containsKey(info.filePath);
                    //判断是否可选择
                    if (listener.itemCheckEnabled(position,isChecked)){
                        listener.onItemCheck(position,isChecked);
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
        });
        //判断文件类型
        if (MediaFileTypeUtils.isVideoFileType(info.filePath)){
            holder.setVisible(R.id.iv_videoPlayIcon,true);
        }else{
            holder.setVisible(R.id.iv_videoPlayIcon,false);
        }
        //判断是否有缩略图（如果视频没有缩略图，用图片加载框架加载第一帧图片）
        if (TextUtils.isEmpty(info.thumbPath)){
            MediaSelectorManager.getInstance().displayImage(mContext,info.filePath, (ImageView) holder.getView(R.id.iv_photo));
        }else{
            MediaSelectorManager.getInstance().displayImage(mContext,info.thumbPath, (ImageView) holder.getView(R.id.iv_photo));
        }
        //判断CheckBox是否可用
        ImageView iv_check = holder.getView(R.id.iv_check);
        if (!config.multiSelect){//单选隐藏
            iv_check.setVisibility(View.GONE);
        }else{
            //判断是否已经选择
            if (DVMediaSelectActivity.map_cacheSelectInfo.containsKey(info.filePath)){
                int checkIcon = config.checkIconResource != 0 ? config.checkIconResource : R.mipmap.icon_dv_checked;
                iv_check.setImageResource(checkIcon);
            }else{
                int unCheckIcon = config.unCheckIconResource != 0 ? config.unCheckIconResource : R.mipmap.icon_dv_unchecked;
                iv_check.setImageResource(unCheckIcon);
            }

        }
    }

    /**
     * 子菜单选择回调
     */
    public interface OnItemCheckListener{
        /**
         * 子菜单选择
         * @param position
         * @param isChecked
         */
        void onItemCheck(int position,boolean isChecked);

        /**
         * 子菜单是否可选择
         * @param position
         */
        boolean itemCheckEnabled(int position,boolean isChecked);
    }
}
