package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.CreateWorkActivity;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import java.util.List;

/**
 * 原版教辅 页码适配器
 * Created by Administrator on 2018/1/17.
 */

public class PageAdapter extends CommonAdapter<Integer> {

    private String         mSectionId;
    private int uploadType;
    private CatalogAdapter mCatalogAdapter;
    private CreateWorkActivity mCreateWorkActivity;


    /*public*/ PageAdapter(Context context, List<Integer> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getLayoutId() {
        if (GlobalData.isPad()) {
            return R.layout.item_page;
        } else {
            return R.layout.item_page_phone;
        }
    }

    @Override
    protected void convert(ViewHolder helper, int position, final Integer item) {
        helper.setText(R.id.tv_page, String.valueOf(item));
        final LinearLayout llPage = helper.getView(R.id.ll_page);
        final String key = mSectionId + "_" + item;
        llPage.setActivated(mCatalogAdapter.mHashMap.containsKey(key));
        llPage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCatalogAdapter.mHashMap.containsKey(key)) {
                    mCatalogAdapter.mHashMap.remove(key);
                    llPage.setActivated(false);
                    mCreateWorkActivity.checkPageSize();
                } else if ((mCatalogAdapter.mHashMap.size() < 3 && AppConst.UPLOAD_TYPE_CAMERA == uploadType) || (AppConst.UPLOAD_TYPE_MARKED == uploadType&&mCatalogAdapter.mHashMap.size() < 20) ) {
                    mCatalogAdapter.mHashMap.put(key, item);
                    llPage.setActivated(true);
                    mCreateWorkActivity.checkPageSize();
                } else {
                    ToastUtils.showShort(mContext, AppConst.UPLOAD_TYPE_CAMERA == uploadType?"不能添加超过三页":"不能添加超过二十页");
                }
            }
        });
    }

    public void setData(List<Integer> questions) {
        mDatas.clear();
        mDatas.addAll(questions);
    }

    /*public*/ void setParentAdapter(CatalogAdapter catalogAdapter) {
        mCatalogAdapter = catalogAdapter;
    }


    /*public*/ void setSectionId(String sectionId) {
        this.mSectionId = sectionId;
    }


    /*public*/ void setActvity(CreateWorkActivity createWorkActivity,int uploadType) {
        mCreateWorkActivity = createWorkActivity;
        this.uploadType = uploadType;
    }
}
