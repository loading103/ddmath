package com.tsinghuabigdata.edu.ddmath.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.MyCourse;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/2/17.
 */

public class MonthUploadAdatper extends CommonAdapter<MyCourse> {

    String[] weeks = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    public MonthUploadAdatper(Context context, List<MyCourse> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_month_upload;
    }

    @Override
    protected void convert(ViewHolder helper, int position, MyCourse item) {
        /*LinearLayout layoutmonthUpload = helper.getView(R.id.ll_month_upload);
        ViewGroup.LayoutParams layoutParams = layoutmonthUpload.getLayoutParams();
        int width = WindowUtils.getScreenWidth(mContext) - DensityUtils.dp2px(mContext, 435);
        layoutParams.height = width * 109 / 120 / 5;
        layoutmonthUpload.setLayoutParams(layoutParams);*/
        String day = item.getDate().substring(8, 10);
        if (item.getDate().equals(DateUtils.getToday())) {
            helper.setText(R.id.tv_day, "今日");
        } else {
            helper.setText(R.id.tv_day, day);
        }
        RelativeLayout rlDay = helper.getView(R.id.rl_day);
        LinearLayout llNormal = helper.getView(R.id.ll_count_normal);
        LinearLayout llEx = helper.getView(R.id.ll_count_ex);
        TextView tvAbout = helper.getView(R.id.tv_about);
        if (item.getCheckStatus() == 0) {
//            rlDay.setActivated(true);
            rlDay.setBackgroundResource(R.drawable.bg_rl_month_upload_red);
            llNormal.setVisibility(View.GONE);
            llEx.setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_count_predict, item.getQuestionCount() + "");
        } else {
//            rlDay.setActivated(false);
            rlDay.setBackgroundResource(R.drawable.bg_rl_month_upload_green);
            llNormal.setVisibility(View.VISIBLE);
            llEx.setVisibility(View.GONE);
            if (item.getRelationStatus() == 0) {
                tvAbout.setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_count, item.getQuestionCount() + "");
            } else {
                tvAbout.setVisibility(View.GONE);
                helper.setText(R.id.tv_count, item.getRelationCount() + "");
            }

        }

        try {
            String week = weeks[item.getWeekDay()];
            helper.setText(R.id.tv_week, week);
        } catch (Exception e) {
            AppLog.i("",e);
            helper.setText(R.id.tv_week, null);
        }
    }
}
