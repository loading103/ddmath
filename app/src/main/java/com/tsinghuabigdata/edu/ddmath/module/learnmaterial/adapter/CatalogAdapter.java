package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.adapter;

import android.content.Context;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.CreateWorkActivity;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.SectionListBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.MultiGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 原版教辅 章节适配器
 * Created by Administrator on 2018/1/17.
 */

public class CatalogAdapter extends CommonAdapter<SectionListBean> {

    private int num;
    private int singleSpace;
    private int uploadType;
    public HashMap<String, Integer> mHashMap = new HashMap<>();
    private CreateWorkActivity mCreateWorkActivity;

    public CatalogAdapter(Context context, List<SectionListBean> mDatas) {
        super(context, mDatas);
    }

    public void setParam(int num, int singleSpace, int uploadType) {
        this.num = num;
        this.singleSpace = singleSpace;
        this.uploadType = uploadType;
    }

    @Override
    protected int getLayoutId() {
        if (GlobalData.isPad()) {
            return R.layout.item_catalog;
        } else {
            return R.layout.item_catalog_phone;
        }
    }

    @Override
    protected void convert(ViewHolder helper, int position, final SectionListBean item) {
        helper.setText(R.id.tv_chapter_name, item.getSectionName());
        MultiGridView gridView = helper.getView(R.id.gv_page);
        gridView.setNumColumns(num);
        gridView.setHorizontalSpacing(singleSpace);
        List<Integer> teamplist = new ArrayList<>();
        teamplist.addAll(item.getPageList());
        PageAdapter adapter = new PageAdapter(mContext, teamplist);
        gridView.setAdapter(adapter);
        adapter.setParentAdapter(this);
        adapter.setActvity(mCreateWorkActivity,uploadType);
        adapter.setSectionId(item.getSectionId());
    }

    public void clearMap() {
        mHashMap.clear();
    }

    public void setActvity(CreateWorkActivity createWorkActivity) {
        mCreateWorkActivity = createWorkActivity;
    }
}
