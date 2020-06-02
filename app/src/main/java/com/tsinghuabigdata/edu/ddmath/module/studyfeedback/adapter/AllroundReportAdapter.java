package com.tsinghuabigdata.edu.ddmath.module.studyfeedback.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.bean.ReportInfo;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.List;

/**
 * Created by Administrator on 2018/3/23.
 */

public class AllroundReportAdapter extends CommonAdapter<ReportInfo> {

    public AllroundReportAdapter(Context context, List<ReportInfo> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getLayoutId() {
        return GlobalData.isPad() ? R.layout.item_report_integrated : R.layout.item_report_integrated_mobile;
    }

    @Override
    protected void convert(ViewHolder helper, int position, ReportInfo item) {
        TextView tvDate = helper.getView(R.id.tv_date);
        TextView tvTitle = helper.getView(R.id.tv_title);
        ImageView ivRedpoint = helper.getView(R.id.iv_redpoint);

        ReportInfo reportInfo = getItem(position);
        String datestr = DateUtils.ms2datestrAsMin(reportInfo.getCreateTime());
        tvDate.setText(datestr);
        String reportName = reportInfo.getReportName();
        //LogUtils.i("index=" + position + " reportName=" + reportName);
        tvTitle.setText(reportName);

        if (reportInfo.isUnread()) {
            ivRedpoint.setVisibility(View.VISIBLE);
        } else {
            ivRedpoint.setVisibility(View.INVISIBLE);
        }
    }
}
