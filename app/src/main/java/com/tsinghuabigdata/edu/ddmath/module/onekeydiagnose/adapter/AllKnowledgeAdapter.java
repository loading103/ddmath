package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.adapter;

import android.content.Context;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.KnowledgesBean;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.MainKnowledgesBean;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.SubKnowledgesBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.MultListView;

import java.util.List;

/**
 * 全部知识点展示适配器
 * Created by Administrator on 2018/8/1.
 */

public class AllKnowledgeAdapter extends CommonAdapter<MainKnowledgesBean> {

    public AllKnowledgeAdapter(Context context, List<MainKnowledgesBean> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getLayoutId() {
        return GlobalData.isPad() ? R.layout.item_all_knowledge : R.layout.item_all_knowledge_phone;
    }

    @Override
    protected void convert(ViewHolder helper, int position, MainKnowledgesBean item) {
        helper.setText(R.id.tv_point_name, item.getKnowledgePointName());
        MultListView listView = helper.getView(R.id.lv_inner_knowledge);
        InnerKnowledgeAdapter adapter = new InnerKnowledgeAdapter(mContext, item.getSubKnowledges());
        listView.setAdapter(adapter);
    }
}
