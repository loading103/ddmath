package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.MonthSumbitedItem;
import com.tsinghuabigdata.edu.ddmath.bean.SubmitQuestion;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDUploadActivity;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.HelpUtil;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.MultiGridView;

import java.util.ArrayList;
import java.util.List;


/**
 * 豆豆检查作业适配器
 * Created by Administrator on 2018/1/30.
 */

public class SubmitedCheckWorkAdatper extends BaseAdapter {

//    private int                     num;
//    private int                     singleSpace;
    private Context                 mContext;
    private List<MonthSumbitedItem> mDatas;

    public SubmitedCheckWorkAdatper(Context context, List<MonthSumbitedItem> datas) {
        mContext = context;
        mDatas = datas;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        return 1;
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        if (mDatas != null) {
            return mDatas.size() + 1;
        }
        return 1;
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
        if (getItemViewType(position) == 0) {
            convertView = View.inflate(mContext, GlobalData.isPad() ? R.layout.item_check_work_title : R.layout.item_check_work_title_phone, null);
            TextView mTvDiagnoseDetail =  convertView.findViewById(R.id.tv_diagnose_detail);
            ImageView helpView = convertView.findViewById(R.id.iv_how_use);
            helpView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HelpUtil.showHelpActivity( mContext, "自由练使用说明", "Q010");
                }
            });
            if (mDatas == null || mDatas.size() == 0) {
                mTvDiagnoseDetail.setVisibility(View.GONE);
            } else {
                mTvDiagnoseDetail.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
        ViewHolder holder;
        if (convertView == null) {
            // 没有复用
            convertView = View.inflate(mContext, GlobalData.isPad() ? R.layout.item_submited_check_work : R.layout.item_submited_check_work_phone, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            // 给holder找view
            holder.tvMonth =  convertView.findViewById(R.id.tv_submit_month);
            holder.gridView =  convertView.findViewById(R.id.gv_submited);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MonthSumbitedItem item = mDatas.get(position - 1);
        // 给holder中的view设置数据
        holder.tvMonth.setText(item.getMonth());

        final List<SubmitQuestion> teamplist = new ArrayList<>();
        teamplist.addAll(item.getQuestions());
        MonthSubmitedCheckWorkAdatper adapter = new MonthSubmitedCheckWorkAdatper(mContext, teamplist);
        holder.gridView.setAdapter(adapter);
        holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SubmitQuestion submitQuestion = teamplist.get(position);
                if (submitQuestion != null) {
                    Intent intent = new Intent(mContext, DDUploadActivity.class);
                    intent.putExtra(DDUploadActivity.PARAM_DDWORKID, submitQuestion.getExamId());
                    intent.putExtra(DDUploadActivity.PARAM_TITLE, submitQuestion.getExamName());
                    mContext.startActivity(intent);
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView      tvMonth;
        MultiGridView gridView;
    }
}
