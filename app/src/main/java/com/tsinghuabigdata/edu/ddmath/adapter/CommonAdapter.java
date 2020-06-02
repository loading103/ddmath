package com.tsinghuabigdata.edu.ddmath.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/12/9.
 */

public abstract class CommonAdapter<T> extends BaseAdapter {
    protected       Context mContext;
    protected       List<T> mDatas;
    protected final int     mItemLayoutId;

    public CommonAdapter(Context context, List<T> mDatas) {
        this.mContext = context;
        this.mDatas = mDatas;
        this.mItemLayoutId = getLayoutId();
    }

    @Override
    public int getCount() {
        if (mDatas != null) {
            return mDatas.size();
        }
        return 0;
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder = getViewHolder(position, convertView,
                parent);
        convert(viewHolder, position, getItem(position));
        return viewHolder.getConvertView();

    }

    protected abstract int getLayoutId();

    protected abstract void convert(ViewHolder helper, int position, T item);

    private ViewHolder getViewHolder(int position, View convertView,
                                     ViewGroup parent) {
        return ViewHolder.get(mContext, convertView, parent, mItemLayoutId,
                position);
    }

}


