package com.devil.library.media.ui.fragment;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.devil.library.media.MediaSelectorManager;
import com.devil.library.media.R;
import com.devil.library.media.adapter.FolderLVAdapter;
import com.devil.library.media.adapter.MediaRVAdapter;
import com.devil.library.media.base.BaseRVAdapter;
import com.devil.library.media.base.EasyRVHolder;
import com.devil.library.media.bean.FolderInfo;
import com.devil.library.media.bean.MediaInfo;
import com.devil.library.media.listener.OnItemClickListener;
import com.devil.library.media.config.DVListConfig;
import com.devil.library.media.enumtype.DVMediaType;
import com.devil.library.media.utils.DisplayUtils;
import com.devil.library.media.utils.FileUtils;
import com.devil.library.media.utils.LayoutManagerHelper;
import com.devil.library.media.utils.MediaDataUtils;
import com.miyouquan.library.DVPermissionUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * 媒体数据列表
 */
public class MediaListFragment extends Fragment {
    //自定义所有文件的文件夹名称
    private static final String fileAll = "所有文件";
    //上下文
    public FragmentActivity mContext;
    //显示的视图
    public View mContentView;
    //列表
    private RecyclerView rv_content;
    //配置
    private DVListConfig config;
    //列表适配器
    private MediaRVAdapter adapter;
    //文件夹选择pop窗口
    private ListPopupWindow folderPopupWindow;
    //文件夹列表适配器
    private FolderLVAdapter folderListAdapter;
    //菜单点击监听者
    private OnItemClickListener onItemClickListener;

    //所有文件内容（key：文件目录 value：文件信息列表）
    private HashMap<String, ArrayList<MediaInfo>> map_allMedia;
    //文件夹名称list
    private ArrayList<FolderInfo> li_folder;
    //文件总数
    private int fileCount = 0;

    //当前文件夹显示的数据内容
    private ArrayList<MediaInfo> currentListContent;


    /**
     * 获取实例
     * @return
     */
    public static MediaListFragment instance() {
        return new MediaListFragment();
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
            mContentView = inflater.inflate(R.layout.fragment_dv_media_list,null);
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
     * 初始化view
     */
    private void initView(){
        rv_content = findViewById(R.id.rv_content);
        // 设置布局管理器
        rv_content.setLayoutManager(LayoutManagerHelper.getGridLayoutManager(mContext,config.listSpanCount,GridLayout.VERTICAL));
        //解决RecyclerView notifyItem闪屏问题
        if (rv_content.getItemAnimator() != null) {
            ((SimpleItemAnimator) rv_content.getItemAnimator()).setSupportsChangeAnimations(false);
        }
        rv_content.addItemDecoration(new RecyclerView.ItemDecoration() {
            int spacing = DisplayUtils.dip2px(mContext, 3);
            int halfSpacing = spacing >> 1;

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = halfSpacing;
                outRect.right = halfSpacing;
                outRect.top = halfSpacing;
                outRect.bottom = halfSpacing;
            }
        });
    }

    /**
     * 设置菜单点击事件监听者
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置适配器
     */
    private void setUpAdapter(){
        //创建适配器
        adapter = new MediaRVAdapter(mContext,currentListContent);
        //设置监听者
        if (onItemClickListener != null){
            adapter.setOnItemCheckListener(new MediaRVAdapter.OnItemCheckListener() {
                @Override
                public void onItemCheck(int position, boolean isChecked) {
                    MediaInfo info = currentListContent.get(position);
                    onItemClickListener.onItemCheck(info,isChecked);
                }

                @Override
                public boolean itemCheckEnabled(int position,boolean isChecked) {
                    return onItemClickListener.itemCheckEnabled(position,isChecked);
                }
            });
            adapter.setOnItemClickListener(new BaseRVAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(EasyRVHolder holder, int position) {
                    onItemClickListener.onItemClick(currentListContent,position);
                }
            });
        }
        //设置列表适配器
        rv_content.setAdapter(adapter);

    }

    /**
     * 刷新数据
     */
    public void refreshData(){
        if (adapter != null)
        adapter.notifyDataSetChanged();
    }

    /**
     * 刷新某个位置的数据
     */
    public void refreshData(int position){
        if (adapter != null)
            adapter.notifyItemChanged(position);
    }

    /**
     * 创建文件
     * @param anchorView
     * @param width
     * @param height
     */
    private void createFolderListPopWindow(View anchorView,int width, int height) {
        folderPopupWindow = new ListPopupWindow(getActivity());
        folderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        folderPopupWindow.setContentWidth(width);
        folderPopupWindow.setWidth(width);
        folderPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        folderPopupWindow.setAnchorView(anchorView);
        folderPopupWindow.setModal(true);
        folderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //设置当前文件夹选择的index
                folderListAdapter.setSelectIndex(i);
                //关闭pop窗口
                folderPopupWindow.dismiss();
                //清除当前显示的list
                currentListContent.clear();
                //增加新选择文件夹的数据
                currentListContent.addAll(map_allMedia.get(li_folder.get(i).folderPath));
                //更新界面
                adapter.notifyDataSetChanged();
                //回调
                if (onItemClickListener != null){
                    onItemClickListener.onFolderCheck(li_folder.get(i));
                }
            }
        });
        folderPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });

        //设置文件夹适配器
        folderListAdapter = new FolderLVAdapter(mContext,li_folder);
        folderPopupWindow.setAdapter(folderListAdapter);
    }

    /**
     * 打开选择文件
     */
    public void openFolderPopupWindow(View anchorView){
        WindowManager wm = getActivity().getWindowManager();
        final int size = wm.getDefaultDisplay().getWidth() / 3 * 2;
        if (folderPopupWindow == null) {
            createFolderListPopWindow(anchorView,size, size);
        }

        if (folderPopupWindow.isShowing()) {
            folderPopupWindow.dismiss();
        } else {
            folderPopupWindow.show();
            if (folderPopupWindow.getListView() != null) {
                folderPopupWindow.getListView().setDivider(new ColorDrawable(Color.BLACK));
            }
            int index = folderPopupWindow.getSelectedItemPosition();
            index = index == 0 ? index : index - 1;
            folderPopupWindow.getListView().setSelection(index);

            setBackgroundAlpha(0.3f);
        }
    }

    /**
     * 设置窗口Alpha
     * @param bgAlpha
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getActivity().getWindow().setAttributes(lp);
    }


    /**
     * 初始化数据
     */
    private void initData(){
        //初始化数据数组
        currentListContent = new ArrayList<>();
        li_folder = new ArrayList<>();
        map_allMedia = new HashMap<>();

        //检查权限并开始
        checkPermissionAndStart();
    }

    /**
     * 检查权限并开始
     */
    private void checkPermissionAndStart(){
        //判断是否有权限操作
        String[] permissions = DVPermissionUtils.arrayConcatAll(DVPermissionUtils.PERMISSION_CAMERA,DVPermissionUtils.PERMISSION_FILE_STORAGE,DVPermissionUtils.PERMISSION_MICROPHONE);
        if (!DVPermissionUtils.verifyHasPermission(mContext,permissions)){
            DVPermissionUtils.requestPermissions(mContext, permissions, new DVPermissionUtils.OnPermissionListener() {
                @Override
                public void onPermissionGranted() {
                    //加载数据
                    loadData();
                }

                @Override
                public void onPermissionDenied() {
                    Toast.makeText(mContext,getString(R.string.permission_denied_tip),Toast.LENGTH_SHORT).show();
                    mContext.finish();
                }
            });
        }else{
            //加载数据
            loadData();
        }
    }

    /**
     * 加载数据
     */
    public void loadData(){
        //判断配置类型
        if (config.mediaType == DVMediaType.PHOTO){//加载图片数据
            MediaDataUtils.getAllPhotoInfo(mContext, new MediaDataUtils.OnLoadCallBack() {
                @Override
                public void onLoadSuccess(HashMap<String, ArrayList<MediaInfo>> allPhotos) {
                    afterLoadDataSuccess(allPhotos,null);
                }
            });
        }else if (config.mediaType == DVMediaType.VIDEO){//加载视频数据
            MediaDataUtils.getAllVideoInfo(mContext,config.quickLoadVideoThumb, new MediaDataUtils.OnLoadCallBack() {
                @Override
                public void onLoadSuccess(HashMap<String, ArrayList<MediaInfo>> allVideos) {
                    afterLoadDataSuccess(null,allVideos);
                }
            });
        }else{//加载所有
            MediaDataUtils.getAllPhotoInfo(mContext, new MediaDataUtils.OnLoadCallBack() {
                @Override
                public void onLoadSuccess(final HashMap<String, ArrayList<MediaInfo>> allPhotos) {
                    MediaDataUtils.getAllVideoInfo(mContext,config.quickLoadVideoThumb, new MediaDataUtils.OnLoadCallBack() {
                        @Override
                        public void onLoadSuccess(HashMap<String, ArrayList<MediaInfo>> allVideos) {
                            afterLoadDataSuccess(allPhotos,allVideos);
                        }
                    });
                }
            });
        }
    }

    /**
     * 加载数据成功后执行的操作
     * @param allPhotos
     * @param allVideos
     */
    private void afterLoadDataSuccess(HashMap<String, ArrayList<MediaInfo>> allPhotos,HashMap<String, ArrayList<MediaInfo>> allVideos){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, ArrayList<MediaInfo>> allMedia = getRealAllMedia(allPhotos,allVideos);

                //所有文件的list
                ArrayList<MediaInfo> li_AllInfo = new ArrayList<>();

                //获取数据
                Set<String> keySet = allMedia.keySet();
                Iterator<String> iterator = keySet.iterator();
                while(iterator.hasNext()){
                    //获取key
                    String key = iterator.next();
                    //获取文件夹所存列表信息
                    ArrayList<MediaInfo> li_info = allMedia.get(key);
                    //加入到map
                    map_allMedia.put(key,li_info);
                    //保存文件夹名称
                    li_folder.add(FolderInfo.createInstance(FileUtils.getFileName(key),key,li_info.size()));
                    //计算总数
                    fileCount += li_info.size();
                    //把子文件夹的list加到全部文件的list
                    li_AllInfo.addAll(li_info);
                }
                MediaDataUtils.sortByModifiedTime(li_AllInfo);
                //增加 所有文件 选择
                map_allMedia.put(fileAll,li_AllInfo);
                //增加 所有文件夹名称
                li_folder.add(0,FolderInfo.createInstance(fileAll,fileAll,fileCount));

                //设置当前需要显示的list
                currentListContent.addAll(li_AllInfo);

                //设置适配器
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //设置适配器
                        setUpAdapter();
                    }
                });
            }
        }).start();

    }

    /**
     * 获取选择的数据
     * @param allPhotos 选择的图片数据
     * @param allVideos 选择的视频数据
     * @return 最后选择的数据
     */
    private HashMap<String, ArrayList<MediaInfo>> getRealAllMedia(HashMap<String, ArrayList<MediaInfo>> allPhotos,HashMap<String, ArrayList<MediaInfo>> allVideos){
        if (allPhotos != null && allVideos == null){
            return allPhotos;
        }else if(allPhotos == null && allVideos != null){
            return allVideos;
        }else{
            HashMap<String, ArrayList<MediaInfo>> allFile = new HashMap<>();
            allFile.putAll(allPhotos);
            Set<String> keySet = allVideos.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                if (allFile.get(key) != null){
                    ArrayList<MediaInfo> sourceArray = allFile.get(key);
                    ArrayList<MediaInfo> videoArray = allVideos.get(key);
                    sourceArray.addAll(videoArray);
                    MediaDataUtils.sortByModifiedTime(sourceArray);
                    allFile.put(key,sourceArray);
                }else{
                    allFile.put(key,allVideos.get(key));
                }
            }
            return allFile;
        }
    }

}
