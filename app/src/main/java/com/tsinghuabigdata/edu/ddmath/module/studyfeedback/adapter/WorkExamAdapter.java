package com.tsinghuabigdata.edu.ddmath.module.studyfeedback.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.ReportInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkUtil;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ReportUtils;

import java.util.Locale;

//作业/考试报告
public class WorkExamAdapter extends ArrayAdapter<ReportInfo> {

    public WorkExamAdapter(Context context) {
        super(context, 0);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(GlobalData.isPad() ? R.layout.item_report_workexam : R.layout.item_report_workexam_phone, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
        viewHolder.bindView(position);
        return convertView;
    }

    //------------------------------------------------------------------------------------------
    class ViewHolder implements View.OnClickListener {

        private int position;

        private LinearLayout mainLayout;
        private ImageView    redView;
        //private ImageView    typeView;
        private TextView     nameView;

        private TextView rightView;
        private TextView wrongView;

        private ViewHolder(View convertView) {
            mainLayout = convertView.findViewById(R.id.item_work_mainlayout);
            mainLayout.setOnClickListener(this);
            redView = convertView.findViewById(R.id.iv_redpoint);
            //typeView = (ImageView) convertView.findViewById(R.id.item_work_typeimage);
            nameView = convertView.findViewById(R.id.item_work_name);

            rightView = convertView.findViewById(R.id.item_work_rightcount);
            wrongView = convertView.findViewById(R.id.item_work_wrongcount);

        }

        void bindView(int position) {
            this.position = position;
            ReportInfo item = getItem(position);
            if (item == null)
                return;

            //未读状态
            redView.setVisibility(item.isUnread() ? View.VISIBLE : View.INVISIBLE);

            //作业名称
            nameView.setText(item.getReportName());

            //作业结果
            String reportType = item.getReportType();
            if ("exerhReport".equals(reportType)) {
                rightView.setText( String.format(Locale.getDefault(),"正确%d题", item.getRightQuestionCount()));
                wrongView.setText(String.format(Locale.getDefault(),"错误%d题", item.getWrongQuestionCount()) );
            } else {
                rightView.setText(String.format(Locale.getDefault(),"得分%d分", Math.round(item.getStudentScore())));
                wrongView.setText(String.format(Locale.getDefault(),"总分%d分", Math.round(item.getTotalScore())));
            }
            //作业类型
            Drawable drawable = DDWorkUtil.getWorkTypeDrawable( getContext(), item.getSourceType() );
            nameView.setCompoundDrawables(null, null, drawable, null);//画在右边
        }

        @Override
        public void onClick(View v) {

            ReportInfo reportInfo = getItem(position);
            if (reportInfo == null)
                return;

            reportInfo.setReadStatus(ReportInfo.S_READ);
            notifyDataSetChanged();

            //跳转报告详情
            String classId = reportInfo.getClassId();
            String examId = reportInfo.getExamId();
            String studentId = reportInfo.getStudentId();
            String reportType = reportInfo.getReportType();
            String reportName = reportInfo.getReportName();
            String createTimeText = ReportUtils.getUpdateTimeText(reportInfo.getCreateTime());

            if ("exerhReport".equals(reportType)) {
                MessageUtils.gotoHomeworkReport(getContext(), examId, studentId, classId, reportName, createTimeText);
            } else if ("weekExamReport".equals(reportType)) {
                MessageUtils.gotoWeekWorkReport(getContext(), examId, studentId, classId, reportName, createTimeText);
            }
        }
    }

}
