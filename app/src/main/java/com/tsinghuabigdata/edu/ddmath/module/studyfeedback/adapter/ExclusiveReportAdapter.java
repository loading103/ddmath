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
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.List;
import java.util.Locale;

/**
 * 专属习题报告 2018/3/23. ---培优报告
 */

public class ExclusiveReportAdapter extends CommonAdapter<ReportInfo> {

    public ExclusiveReportAdapter(Context context, List<ReportInfo> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getLayoutId() {
        return GlobalData.isPad() ? R.layout.item_report_exclusive : R.layout.item_report_exclusive_mobile;
    }

    @Override
    protected void convert(ViewHolder helper, int position, ReportInfo item) {
        TextView tvDate = helper.getView(R.id.tv_date);
        TextView tvTitle = helper.getView(R.id.tv_title);
        TextView tvResult = helper.getView( R.id.tv_result );
        ImageView ivRedpoint = helper.getView(R.id.iv_redpoint);
        ImageView paperTypeView = helper.getView(R.id.iv_praticetype);

        ReportInfo reportInfo = getItem(position);
        String datestr = DateUtils.ms2datestrAsMin(reportInfo.getCreateTime());
        tvDate.setText(datestr);
        String reportName = reportInfo.getReportName();
        //周,月 错题精炼增加
        if(AppConst.WORK_TYPE_EBOOKWEEKTRAIN.equals(item.getSourceType()) || AppConst.WORK_TYPE_MONTH_REFINE.equals(item.getSourceType())){
            reportName = "【精选推荐】" + reportName;
        }

        tvTitle.setText(reportName);

        if( reportInfo.getRightQuestionCount()+reportInfo.getWrongQuestionCount() == (int)reportInfo.getTotalScore() ){
            tvResult.setText( String.format(Locale.getDefault(), "正确：%d题，错误：%d题", reportInfo.getRightQuestionCount(), reportInfo.getWrongQuestionCount()));
        }else{
            tvResult.setText( String.format(Locale.getDefault(), "得分：%d  总分：%d", (int)reportInfo.getStudentScore(), (int)reportInfo.getTotalScore()));
        }

        if (reportInfo.isUnread()) {
            ivRedpoint.setVisibility(View.VISIBLE);
        } else {
            ivRedpoint.setVisibility(View.INVISIBLE);
        }

        int paddingleft = DensityUtils.dp2px( mContext, GlobalData.isPad()?36:24);
        if( 1 == reportInfo.getPaperType() ){     //周类型
            paperTypeView.setVisibility( View.VISIBLE );
            paperTypeView.setImageResource( R.drawable.icon_weekreport );
            tvTitle.setPadding(paddingleft,0,0,0);
        }else if( 2 == reportInfo.getPaperType() ){     //自定义类型
            paperTypeView.setVisibility( View.VISIBLE );
            paperTypeView.setImageResource( R.drawable.icon_customreport );
            tvTitle.setPadding(paddingleft,0,0,0);
        }else if( 3 == reportInfo.getPaperType() ){     //月类型
            paperTypeView.setVisibility( View.VISIBLE );
            paperTypeView.setImageResource( R.drawable.icon_monthreport );
            tvTitle.setPadding(paddingleft,0,0,0);
        }else{       //没有类型
            paperTypeView.setVisibility( View.GONE );
            tvTitle.setPadding(0,0,0,0);
        }
    }
}
