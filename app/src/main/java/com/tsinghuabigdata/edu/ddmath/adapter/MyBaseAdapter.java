package com.tsinghuabigdata.edu.ddmath.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyBaseAdapter<T> extends BaseAdapter {
    private static final String TAG = "MyBaseAdapter";
    protected List<T> mDatas;


    public MyBaseAdapter() {
        mDatas = new ArrayList<T>();
    }

    public void set(List<T> datas) {
        if (datas == null) {
            return;
        }
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void add(List<T> datas) {
        if (datas == null) {
            return;
        }
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        mDatas.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        //子类必须实现该方法
        return null;
    }

    public List<T> getDatas() {
        return mDatas;
    }

}
