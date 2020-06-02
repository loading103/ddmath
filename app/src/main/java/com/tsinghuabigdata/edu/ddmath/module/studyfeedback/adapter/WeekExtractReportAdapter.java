package com.tsinghuabigdata.edu.ddmath.module.studyfeedback.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.bean.ReportInfo;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2018/3/23.
 */

public class WeekExtractReportAdapter extends CommonAdapter<ReportInfo> {

    public WeekExtractReportAdapter(Context context, List<ReportInfo> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getLayoutId() {
        return GlobalData.isPad() ? R.layout.item_report_week_extract : R.layout.item_report_week_extract_mobile;
    }

    @Override
    protected void convert(ViewHolder helper, int position, ReportInfo item) {
        TextView tvDate = helper.getView(R.id.tv_date);
        TextView tvTitle = helper.getView(R.id.tv_title);
        TextView tvResult = helper.getView( R.id.tv_result );
        ImageView ivRedpoint = helper.getView(R.id.iv_redpoint);

        ReportInfo reportInfo = getItem(position);
        String datestr = DateUtils.ms2datestrAsMin(reportInfo.getCreateTime());
        tvDate.setText(datestr);
        String reportName = reportInfo.getReportName();
        //周,月 错题精炼增加
        if(AppConst.WORK_TYPE_EBOOKWEEKTRAIN.equals(item.getSourceType()) || AppConst.WORK_TYPE_MONTH_REFINE.equals(item.getSourceType())){
            reportName = "【精选推荐】" + reportName;
        }
        tvTitle.setText(reportName);

        tvResult.setText( String.format(Locale.getDefault(), "正确%d题 错误%d题", reportInfo.getRightQuestionCount(), reportInfo.getWrongQuestionCount()));

        if (reportInfo.isUnread()) {
            ivRedpoint.setVisibility(View.VISIBLE);
        } else {
            ivRedpoint.setVisibility(View.INVISIBLE);
        }
    }
}
