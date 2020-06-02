package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.OriginalBookBean;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.KnowledgesBean;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.SubKnowledgesBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.List;

/**
 * 每个等级的知识点选择适配器
 * Created by Administrator on 2018/8/1.
 */

public class KnowledgeSelectAdapter extends CommonAdapter<SubKnowledgesBean> {


    private SelectKnowledgeListener mSelectKnowledgeListener;

    public KnowledgeSelectAdapter(Context context, List<SubKnowledgesBean> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getLayoutId() {
        return GlobalData.isPad() ? R.layout.item_knowledge_select : R.layout.item_knowledge_select_phone;
    }

    @Override
    protected void convert(ViewHolder helper, int position, final SubKnowledgesBean item) {
        final LinearLayout llRoot = helper.getView(R.id.ll_knowledge_select);
        final CheckBox cb = helper.getView(R.id.cb_knowledge);
        //        TextView tvKnowledgeName = helper.getView(R.id.tv_knowledge_name);
        //        TextView tvAccuracy = helper.getView(R.id.tv_accuracy);
        //        TextView tvCount = helper.getView(R.id.tv_count);
        helper.setText(R.id.tv_knowledge_name, item.getKnowledgePointName());
        helper.setText(R.id.tv_accuracy, "正确率 " + Math.round(item.getAccuracy() * 100) + "%");
        //helper.setText(R.id.tv_count, "错" + item.getWrongCount() + "题/共" + item.getTotalCount() + "题");
        llRoot.setActivated(item.isSelect());
        cb.setChecked(item.isSelect());
        llRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.changeSelect();
                notifyDataSetChanged();
                if (mSelectKnowledgeListener != null) {
                    mSelectKnowledgeListener.select();
                }
            }
        });


    }

    public interface SelectKnowledgeListener {
        void select();
    }

    public void setSelectKnowledgeListener(SelectKnowledgeListener selectKnowledgeListener) {
        mSelectKnowledgeListener = selectKnowledgeListener;
    }
}
