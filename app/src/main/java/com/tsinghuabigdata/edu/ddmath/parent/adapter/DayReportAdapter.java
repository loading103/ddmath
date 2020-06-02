package com.tsinghuabigdata.edu.ddmath.parent.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkUtil;
import com.tsinghuabigdata.edu.ddmath.parent.bean.DayReportBean;
import com.tsinghuabigdata.edu.ddmath.parent.bean.DayReportItemBean;

import java.util.List;
import java.util.Locale;

/**
 * 家长端日报告
 */

public class DayReportAdapter extends BaseAdapter {

    private Context                 mContext;
    private List<DayReportItemBean> mDatas;

    public DayReportAdapter(Context context, List<DayReportItemBean> datas) {
        mContext = context;
        mDatas = datas;
    }

    @Override
    public int getItemViewType(int position) {
        DayReportItemBean item = mDatas.get(position);
        if (item != null && item.getExerStatus() < DayReportBean.TOMAKE) {
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
            return mDatas.size();
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

            ViewHolder1 holder1;
            if (convertView == null) {
                // 没有复用
                convertView = View.inflate(mContext, R.layout.item_day_report_to_submit, null);
                holder1 = new ViewHolder1();
                convertView.setTag(holder1);
                // 给holder找view
                holder1.tvTtile = (TextView) convertView.findViewById(R.id.tv_title);
                holder1.tvQuestionCount = (TextView) convertView.findViewById(R.id.tv_question_count);
                holder1.tvCorrectUpload = (TextView) convertView.findViewById(R.id.tv_correct_upload);
                holder1.tvEndTime = (TextView) convertView.findViewById(R.id.tv_end_time);
                holder1.llOverdueTip = (LinearLayout) convertView.findViewById(R.id.ll_overdue_tip);
            } else {
                holder1 = (ViewHolder1) convertView.getTag();
            }
            DayReportItemBean item = mDatas.get(position);
            //报告名称
            if( DayReportItemBean.PAPER_TYPE_WEEK == item.getPaperType() ){
                holder1.tvTtile.setText( String.format(Locale.getDefault(),"【周】%s",item.getExamName()) );
            }else if( DayReportItemBean.PAPER_TYPE_CUSTOM == item.getPaperType() ){
                holder1.tvTtile.setText( String.format(Locale.getDefault(),"【自定义】%s",item.getExamName()) );
            }else if( DayReportItemBean.PAPER_TYPE_MONTH == item.getPaperType() ) {
                holder1.tvTtile.setText( String.format(Locale.getDefault(),"【月】%s",item.getExamName()) );
            }else if( DayReportItemBean.PAPER_TYPE_EXAM == item.getPaperType() ) {
                holder1.tvTtile.setText( String.format(Locale.getDefault(),"【考前】%s",item.getExamName()) );
            }else{
                holder1.tvTtile.setText(item.getExamName());
            }

            //holder1.tvQuestionCount.setText("共" + item.getQuestionCount() + "题");
            if (item.getType() == DayReportBean.EXAM) {
                holder1.tvQuestionCount.setText(String.format(Locale.getDefault(),"共%d分",item.getTotalScore()));
            } else {
                holder1.tvQuestionCount.setText(String.format(Locale.getDefault(),"共%d题",item.getQuestionCount()));
            }

            //上传类型
            if(AppConst.UPLOAD_TYPE_MARKED == item.getUploadType() ){
                holder1.tvCorrectUpload.setVisibility(View.VISIBLE);
            }else{
                holder1.tvCorrectUpload.setVisibility(View.GONE);
            }

            if (System.currentTimeMillis() < item.getEndTime()) {
                holder1.tvEndTime.setText(DDWorkUtil.getDayReportEndMinute(item.getEndTime()));
                holder1.tvEndTime.setVisibility(View.VISIBLE);
                holder1.llOverdueTip.setVisibility(View.GONE);
            } else {
                holder1.tvEndTime.setVisibility(View.GONE);
                holder1.llOverdueTip.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
        ViewHolder2 holder2;
        if (convertView == null) {
            // 没有复用
            convertView = View.inflate(mContext, R.layout.item_day_report_submited, null);
            holder2 = new ViewHolder2();
            convertView.setTag(holder2);
            // 给holder找view
            holder2.tvTtile = (TextView) convertView.findViewById(R.id.tv_title);
            holder2.tvQuestionCount = (TextView) convertView.findViewById(R.id.tv_question_count);
            holder2.llQuestionCount = (LinearLayout) convertView.findViewById(R.id.ll_question_count);
            holder2.tvCorrecting = (TextView) convertView.findViewById(R.id.tv_correcting);
            holder2.tvRightCount = (TextView) convertView.findViewById(R.id.tv_right_count);
            holder2.tvWrongCount = (TextView) convertView.findViewById(R.id.tv_wrong_count);

            holder2.tvEndTime = (TextView) convertView.findViewById(R.id.tv_end_time);
            holder2.tvOverdue = (TextView) convertView.findViewById(R.id.tv_overdue);
            holder2.tvStatus = (TextView) convertView.findViewById(R.id.tv_status);

            holder2.llQuestionScore = (LinearLayout) convertView.findViewById(R.id.ll_question_score);
            holder2.tvStudentScore = (TextView) convertView.findViewById(R.id.tv_student_score);
        } else {
            holder2 = (ViewHolder2) convertView.getTag();
        }

        DayReportItemBean item = mDatas.get(position);
        //报告名称
        if( DayReportItemBean.PAPER_TYPE_WEEK == item.getPaperType() ){
            holder2.tvTtile.setText( String.format(Locale.getDefault(),"【周】%s",item.getExamName()) );
        }else if( DayReportItemBean.PAPER_TYPE_CUSTOM == item.getPaperType() ){
            holder2.tvTtile.setText( String.format(Locale.getDefault(),"【自定义】%s",item.getExamName()) );
        }else if( DayReportItemBean.PAPER_TYPE_MONTH == item.getPaperType() ) {
            holder2.tvTtile.setText( String.format(Locale.getDefault(),"【月】%s",item.getExamName()) );
        }else if( DayReportItemBean.PAPER_TYPE_EXAM == item.getPaperType() ) {
            holder2.tvTtile.setText( String.format(Locale.getDefault(),"【考前】%s",item.getExamName()) );
        }else{
            holder2.tvTtile.setText(item.getExamName());
        }

        //
        if (item.getType() == DayReportBean.EXAM) {
            holder2.tvQuestionCount.setText(String.format(Locale.getDefault(),"共%d分",item.getTotalScore()));
        } else {
            holder2.tvQuestionCount.setText(String.format(Locale.getDefault(),"共%d题",item.getQuestionCount()));
        }

        if (item.getExerStatus() < DayReportBean.MADE) {
            holder2.tvCorrecting.setVisibility(View.VISIBLE);
            holder2.llQuestionCount.setVisibility(View.GONE);
            holder2.llQuestionScore.setVisibility(View.GONE);
        } else if (item.getType() == DayReportBean.EXAM) {
            holder2.tvCorrecting.setVisibility(View.GONE);
            holder2.llQuestionCount.setVisibility(View.GONE);
            holder2.llQuestionScore.setVisibility(View.VISIBLE);
            holder2.tvStudentScore.setText(String.format(Locale.getDefault(),"得%d分",item.getStudentScore()));
        } else {
            holder2.tvCorrecting.setVisibility(View.GONE);
            holder2.llQuestionCount.setVisibility(View.VISIBLE);
            holder2.llQuestionScore.setVisibility(View.GONE);
            holder2.tvRightCount.setText(String.format(Locale.getDefault(),"%d题",item.getRightCount()) );
            holder2.tvWrongCount.setText(String.format(Locale.getDefault(),"%d题",item.getWrongCount()) );
        }

        if (item.getExerStatus() < DayReportBean.STAT_FINISHED) {
            holder2.tvStatus.setVisibility(View.GONE);
        } else if (item.getExerStatus() == DayReportBean.STAT_FINISHED) {
            holder2.tvStatus.setVisibility(View.VISIBLE);
            holder2.tvStatus.setActivated(false);
            holder2.tvStatus.setText("未阅读");
        } else {
            holder2.tvStatus.setVisibility(View.VISIBLE);
            holder2.tvStatus.setActivated(true);
            holder2.tvStatus.setText("已阅读");
        }
        holder2.tvEndTime.setText(DDWorkUtil.getDayReportSubmiteMinute(item.getSubmitTime()));
        if (item.getSubmitTime() > item.getEndTime()) {
            holder2.tvOverdue.setVisibility(View.VISIBLE);
        } else {
            holder2.tvOverdue.setVisibility(View.GONE);
        }
/*        ViewHolder holder;
        if (convertView == null) {
            // 没有复用
            convertView = View.inflate(mContext, GlobalData.isPad() ? R.layout.item_submited_check_work : R.layout.item_submited_check_work_phone, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            // 给holder找view
            holder.tvMonth = (TextView) convertView.findViewById(R.id.tv_submit_month);
            holder.gridView = (MultiGridView) convertView.findViewById(R.id.gv_submited);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DayReportItemBean item = mDatas.get(position - 1);
        // 给holder中的view设置数据
        holder.tvMonth.setText(item.getMonth());*/

        return convertView;
    }

    class ViewHolder1 {
        TextView     tvTtile;
        TextView     tvQuestionCount;
        TextView     tvCorrectUpload;
        TextView     tvEndTime;
        LinearLayout llOverdueTip;
    }

    class ViewHolder2 {
        TextView     tvTtile;
        TextView     tvQuestionCount;
        LinearLayout llQuestionCount;
        TextView     tvCorrecting;
        TextView     tvRightCount;
        TextView     tvWrongCount;
        TextView     tvEndTime;
        TextView     tvOverdue;
        TextView     tvStatus;

        LinearLayout llQuestionScore;
        TextView     tvStudentScore;
    }
}
