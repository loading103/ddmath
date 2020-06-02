package com.tsinghuabigdata.edu.ddmath.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.MonthSumbitedItem;
import com.tsinghuabigdata.edu.ddmath.bean.SubmitQuestion;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDUploadActivity;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.MultiGridView;

import java.util.ArrayList;
import java.util.List;


/**
 * 已提交校内作业适配器
 * Created by Administrator on 2017/11/29.
 */

public class SubmitedWorkAdatper extends BaseAdapter {

    private Context                 mContext;
    private List<MonthSumbitedItem> mDatas;

    public SubmitedWorkAdatper(Context context, List<MonthSumbitedItem> datas) {
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
            convertView = View.inflate(mContext, GlobalData.isPad() ? R.layout.item_doudou_work_title : R.layout.item_doudou_work_title_phone, null);
            TextView mTvSubmited =  convertView.findViewById(R.id.tv_submited);
            if (mDatas == null || mDatas.size() == 0) {
                mTvSubmited.setVisibility(View.GONE);
            } else {
                mTvSubmited.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
        ViewHolder holder;
        if (convertView == null) {
            // 没有复用
            convertView = View.inflate(mContext, GlobalData.isPad() ? R.layout.item_doudou_work : R.layout.item_doudou_work_phone, null);
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
        MonthSubmitedWorkAdatper adapter = new MonthSubmitedWorkAdatper(mContext, teamplist);
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
