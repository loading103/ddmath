package com.tsinghuabigdata.edu.ddmath.parent.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.SubmitQuestion;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkUtil;
import com.tsinghuabigdata.edu.ddmath.parent.bean.WeekReportItemBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.List;


/**
 * 豆豆检查作业适配器
 * Created by Administrator on 2018/7/3.
 */

public class WeekReportAdatper extends BaseAdapter {


    private Context                  mContext;
    private List<WeekReportItemBean> mDatas;


    public WeekReportAdatper(Context context, List<WeekReportItemBean> datas) {
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
        ViewHolder holder;
        if (convertView == null) {
            // 没有复用
            convertView = View.inflate(mContext, R.layout.item_week_report, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            // 给holder找view
            holder.tvTtile = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tvQuestionCount = (TextView) convertView.findViewById(R.id.tv_question_count);
            holder.tvStatus = (TextView) convertView.findViewById(R.id.tv_status);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        WeekReportItemBean item = mDatas.get(position);
        holder.tvTtile.setText(item.getTitle());
        String data = "本周 " + item.getTotalCount() + "题  正确率 " + Math.round(item.getTotalAccuracy() * 100) + "%  错题未订正 " + item.getUnRevisedCount() + "题";
        holder.tvQuestionCount.setText(data);
        if (item.getCheckStatus() == WeekReportItemBean.UNREAD) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setActivated(false);
            holder.tvStatus.setText("未阅读");
        } else {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setActivated(true);
            holder.tvStatus.setText("已阅读");
        }
        return convertView;
    }

    class ViewHolder {
        TextView tvTtile;
        TextView tvQuestionCount;
        TextView tvStatus;
    }


}
