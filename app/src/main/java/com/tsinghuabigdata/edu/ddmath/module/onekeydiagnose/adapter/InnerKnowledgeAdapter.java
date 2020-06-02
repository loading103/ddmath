package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.SubKnowledgesBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.List;

/**
 * 全部知识点内部 每个等级的知识点展示适配器
 * Created by Administrator on 2018/8/3.
 */

public class InnerKnowledgeAdapter extends CommonAdapter<SubKnowledgesBean> {


    public InnerKnowledgeAdapter(Context context, List<SubKnowledgesBean> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getLayoutId() {
        return GlobalData.isPad() ? R.layout.item_inner_knowledge : R.layout.item_inner_knowledge_phone;
    }

    @Override
    protected void convert(ViewHolder helper, int position, SubKnowledgesBean item) {
        //        TextView tvKnowledgeName = helper.getView(R.id.tv_knowledge_name);
        //        TextView tvAccuracy = helper.getView(R.id.tv_accuracy);
        //        TextView tvCount = helper.getView(R.id.tv_count);
        helper.setText(R.id.tv_knowledge_name, item.getKnowledgePointName());
        helper.setText(R.id.tv_accuracy, "正确率 " + Math.round(item.getAccuracy() * 100) + "%");
        //helper.setText(R.id.tv_count, "错" + item.getWrongCount() + "题/共" + item.getTotalCount() + "题");

    }


}
