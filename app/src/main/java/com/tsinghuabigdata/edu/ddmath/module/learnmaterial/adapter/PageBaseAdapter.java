package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import java.util.List;


/**
 * 原版教辅 页码适配器
 * Created by Administrator on 2018/1/19.
 */

public class PageBaseAdapter extends BaseAdapter {


    private Context       mContext;
    private List<Integer> mDatas;

    private String         mSectionId;
    private CatalogAdapter mCatalogAdapter;


    public PageBaseAdapter(Context context, List<Integer> datas) {
        mContext = context;
        mDatas = datas;
    }


    @Override
    public int getCount() {
        if (mDatas != null) {
            return mDatas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mDatas != null) {
            return mDatas.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 没有复用
        if (GlobalData.isPad()) {
            convertView = View.inflate(mContext, R.layout.item_page, null);
        } else {
            convertView = View.inflate(mContext, R.layout.item_page_phone, null);
        }
        TextView tvPage = (TextView) convertView.findViewById(R.id.tv_page);
        final LinearLayout llPage = (LinearLayout) convertView.findViewById(R.id.ll_page);
        Integer integer = mDatas.get(position);
        tvPage.setText(String.valueOf(integer));
        final String key = mSectionId + "_" + integer;
        /*boolean selected = mCatalogAdapter.mHashMap.containsKey(key);
        llPage.setSelected(selected);
        LogUtils.i("key=" + key + "selected=" + selected);*/

        if (mCatalogAdapter.mHashMap.containsKey(key)){
            llPage.setSelected(true);
        }else {
            llPage.setSelected(false);
        }
        llPage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCatalogAdapter.mHashMap.containsKey(key)) {
                    mCatalogAdapter.mHashMap.remove(key);
                    llPage.setSelected(false);
                } else if (mCatalogAdapter.mHashMap.size() < 3) {
                    mCatalogAdapter.mHashMap.put(key, 1);
                    llPage.setSelected(true);
                } else {
                    ToastUtils.showShort(mContext, "不能添加超过三页");
                }
            }
        });
        return convertView;
    }

    public void setData(List<Integer> list) {
        mDatas.clear();
        mDatas.addAll(list);
    }

    public void setParentAdapter(CatalogAdapter catalogAdapter) {
        mCatalogAdapter = catalogAdapter;
    }


    public void setSectionId(String sectionId) {
        this.mSectionId = sectionId;
    }
}
