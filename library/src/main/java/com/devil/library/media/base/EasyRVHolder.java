package com.devil.library.media.base;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * RecyclerView的简易Holder
 */
public class EasyRVHolder extends RecyclerView.ViewHolder {
    //缓存view
    private SparseArray<View> mViews = new SparseArray<>();
    //显示的view
    private View mContentView;
    //上下文
    protected Context mContext;

    public EasyRVHolder(Context context, View itemView) {
        super(itemView);
        this.mContext = context;
        mContentView = itemView;
    }

    /**
     * 根据view的Id获取view
     * @param viewId
     * @param <V>
     * @return
     */
    public <V extends View> V getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mContentView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (V) view;
    }

    /**
     * 找到控件
     * @param id
     * @param <T>
     * @return
     */
    protected final <T extends View> T findViewById(@IdRes int id) {
        return mContentView.findViewById(id);
    }

    /**
     * 获取item布局
     *
     * @return
     */
    public View getItemView() {
        return mContentView;
    }

    public EasyRVHolder setOnItemViewClickListener(View.OnClickListener listener){
        mContentView.setOnClickListener(listener);
        return this;
    }

    public EasyRVHolder setOnItemViewLongClickListener(View.OnLongClickListener listener){
        mContentView.setOnLongClickListener(listener);
        return this;
    }

    /**
     * 设置textView文本内容
     *
     * @param viewId viewId
     * @param value  文本内容
     * @return viewHolder
     */
    public EasyRVHolder setText(int viewId, String value) {
        TextView view = getView(viewId);
        view.setText(value);
        return this;
    }

    /**
     * 设置textView文本颜色
     *
     * @param viewId viewId
     * @param color  颜色数值
     * @return viewHolder
     */
    public EasyRVHolder setTextColor(int viewId, int color) {
        TextView view = getView(viewId);
        view.setTextColor(color);
        return this;
    }

    /**
     * 设置textView文本颜色
     *
     * @param viewId   viewId
     * @param colorRes 颜色Id
     * @return viewHolder
     */
    public EasyRVHolder setTextColorRes(int viewId, int colorRes) {
        TextView view = getView(viewId);
        view.setTextColor(ContextCompat.getColor(mContext, colorRes));
        return this;
    }

    /**
     * 设置imgView的图片,通过Id设置
     *
     * @param viewId   viewId
     * @param imgResId 图片Id
     * @return viewHolder viewHolder
     */
    public EasyRVHolder setImageResource(int viewId, int imgResId) {
        ImageView view = getView(viewId);
        view.setImageResource(imgResId);
        return this;
    }

    /**
     * 设置背景颜色
     *
     * @param viewId viewId
     * @param color  颜色数值
     * @return viewHolder viewHolder
     */
    public EasyRVHolder setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    /**
     * 设置背景颜色
     *
     * @param viewId   viewId
     * @param colorRes 颜色Id
     * @return viewHolder
     */
    public EasyRVHolder setBackgroundColorRes(int viewId, int colorRes) {
        View view = getView(viewId);
        view.setBackgroundResource(colorRes);
        return this;
    }

    /**
     * 设置img的Drawable
     *
     * @param viewId   viewId
     * @param drawable drawable
     * @return viewHolder
     */
    public EasyRVHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = getView(viewId);
        view.setImageDrawable(drawable);
        return this;
    }

    /**
     * 设置img的Drawable
     *
     * @param viewId      viewId
     * @param drawableRes drawableId
     * @return viewHolder
     */
    public EasyRVHolder setImageDrawableRes(int viewId, int drawableRes) {
        Drawable drawable = ContextCompat.getDrawable(mContext,drawableRes);
        return setImageDrawable(viewId, drawable);
    }

    /**
     * 设置img图片路径
     *
     * @param viewId viewId
     * @param imgUrl 图片路径
     * @return viewHolder
     */
    public EasyRVHolder setImageUrl(int viewId, String imgUrl) {
        // TODO: Use Glide/Picasso/ImageLoader/Fresco
        return this;
    }

    /**
     * 设置img图片Bitmap
     *
     * @param viewId    viewId
     * @param imgBitmap imgBitmap
     * @return viewHolder
     */
    public EasyRVHolder setImageBitmap(int viewId, Bitmap imgBitmap) {
        ImageView view = getView(viewId);
        view.setImageBitmap(imgBitmap);
        return this;
    }

    /**
     * 设置控件是否显示
     *
     * @param viewId  viewId
     * @param visible true(visible)/false(gone)
     * @return viewHolder
     */
    public EasyRVHolder setVisible(int viewId, boolean visible) {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 设置控件是否显示
     *
     * @param viewId  viewId
     * @param visible visible,invisible,gone
     * @return viewHolder
     */
    public EasyRVHolder setVisible(int viewId, int visible) {
        View view = getView(viewId);
        view.setVisibility(visible);
        return this;
    }

    /**
     * 设置控件的tag
     *
     * @param viewId viewId
     * @param tag    tag
     * @return viewHolder
     */
    public EasyRVHolder setTag(int viewId, Object tag) {
        View view = getView(viewId);
        view.setTag(tag);
        return this;
    }

    /**
     * 设置控件tag
     *
     * @param viewId viewId
     * @param key    tag的key
     * @param tag    tag
     * @return viewHolder
     */
    public EasyRVHolder setTag(int viewId, int key, Object tag) {
        View view = getView(viewId);
        view.setTag(key, tag);
        return this;
    }

    /**
     * 设置Checkable控件的选择情况
     *
     * @param viewId  viewId
     * @param checked 选择
     * @return viewHolder
     */
    public EasyRVHolder setChecked(int viewId, boolean checked) {
        Checkable view = getView(viewId);
        view.setChecked(checked);
        return this;
    }

    /**
     * 设置控件透明效果
     *
     * @param viewId viewId
     * @param value  透明值
     * @return viewHolder
     */
    public EasyRVHolder setAlpha(int viewId, float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getView(viewId).setAlpha(value);
        } else {
            AlphaAnimation alpha = new AlphaAnimation(value, value);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            getView(viewId).startAnimation(alpha);
        }
        return this;
    }

    /**
     * 设置TextView字体
     *
     * @param viewId   viewId
     * @param typeface typeface
     * @return viewHolder
     */
    public EasyRVHolder setTypeface(int viewId, Typeface typeface) {
        TextView view = getView(viewId);
        view.setTypeface(typeface);
        view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        return this;
    }

    /**
     * 设置多个TextView字体
     *
     * @param typeface typeface
     * @param viewIds  viewId组合
     * @return viewHolder
     */
    public EasyRVHolder setTypeface(Typeface typeface, int... viewIds) {
        for (int viewId : viewIds) {
            TextView view = getView(viewId);
            view.setTypeface(typeface);
            view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }
        return this;
    }

    /**
     * 设置监听
     *
     * @param viewId
     * @param listener
     * @return
     */
    public EasyRVHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    /**
     * 设置CheckBox监听
     * @param viewId
     * @param listener
     * @return
     */
    public EasyRVHolder setOnCheckedChangeListener(int viewId, CompoundButton.OnCheckedChangeListener listener) {
        CheckBox view = getView(viewId);
        view.setOnCheckedChangeListener(listener);
        return this;
    }
}
