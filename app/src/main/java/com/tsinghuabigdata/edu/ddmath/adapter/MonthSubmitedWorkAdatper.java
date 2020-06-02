package com.tsinghuabigdata.edu.ddmath.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.SubmitQuestion;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkUtil;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.DDWorkDetail;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.List;
import java.util.Locale;


/**
 * 已提交校内作业适配器
 * Created by Administrator on 2017/11/29.
 */

public class MonthSubmitedWorkAdatper extends BaseAdapter {


    private Context              mContext;
    private List<SubmitQuestion> mDatas;


    /*public*/ MonthSubmitedWorkAdatper(Context context, List<SubmitQuestion> datas) {
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
            if (GlobalData.isPad()) {
                convertView = View.inflate(mContext, R.layout.item_month_submited, null);
            } else {
                convertView = View.inflate(mContext, R.layout.item_month_submited_phone, null);
            }
            holder = new ViewHolder();
            convertView.setTag(holder);
            // 给holder找view
            holder.llQuestionScore =  convertView.findViewById(R.id.ll_question_score);
            holder.tvStudentScore =  convertView.findViewById(R.id.tv_student_score);
            holder.tvTotalScore =  convertView.findViewById(R.id.tv_total_score);
            holder.llQuestionCount =  convertView.findViewById(R.id.ll_question_count);
            holder.llCorrecting =  convertView.findViewById(R.id.ll_correcting);
            holder.tvRightCount =  convertView.findViewById(R.id.tv_right_count);
            holder.tvWrongCount =  convertView.findViewById(R.id.tv_wrong_count);
            holder.tvTitle =  convertView.findViewById(R.id.tv_title);
            holder.tvEndTime =  convertView.findViewById(R.id.tv_end_time);
            holder.tvSubmitCount =  convertView.findViewById(R.id.tv_submit_count);
            holder.ivOverdue =  convertView.findViewById(R.id.iv_overdue);
            holder.correctingView =  convertView.findViewById(R.id.tv_correcting_textview);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SubmitQuestion item = mDatas.get(position);
        // 给holder中的view设置数据
        ////  2017/10/20 模拟批阅状态
        //        item.setExerStatus(3 + new Random().nextInt(2));
        holder.tvTitle.setText(item.getExamName());
        Drawable typeDrawable = DDWorkUtil.getWorkTypeDrawable(mContext,item.getResourceType());
        holder.tvTitle.setCompoundDrawables(null, null, typeDrawable, null);//作业类型 画在右边

        if (item.getExerStatus() < SubmitQuestion.CORRECTED) {
            holder.llQuestionScore.setVisibility(View.INVISIBLE);
            holder.llQuestionCount.setVisibility(View.INVISIBLE);
            holder.llCorrecting.setVisibility(View.VISIBLE);

            boolean buy = item.getUsePrivilege()== DDWorkDetail.ST_BUYED;
            String data = item.getUploadType()==AppConst.UPLOAD_TYPE_MARKED?"报告生成中":(buy?"批阅中":"等待老师批阅");
            holder.correctingView.setText( data );
        } else if (AppConst.WORK_TYPE_WEEKTRAIN.equals(item.getResourceType())) {
            holder.llQuestionScore.setVisibility(View.VISIBLE);
            holder.llQuestionCount.setVisibility(View.INVISIBLE);
            holder.llCorrecting.setVisibility(View.INVISIBLE);
            holder.tvStudentScore.setText(String.format(Locale.getDefault(),"%d分",item.getStudentScore() ));
            holder.tvTotalScore.setText(String.format(Locale.getDefault(),",共%d分",item.getTotalScore() ));
        } else {
            holder.llQuestionScore.setVisibility(View.INVISIBLE);
            holder.llQuestionCount.setVisibility(View.VISIBLE);
            holder.llCorrecting.setVisibility(View.INVISIBLE);
            holder.tvRightCount.setText(String.format(Locale.getDefault(),"%d题",item.getRightQuestionCount() ));
            holder.tvWrongCount.setText(String.format(Locale.getDefault(),"%d题",item.getWrongQuestionCount() ));
        }

        holder.tvEndTime.setText(DDWorkUtil.getEndMinute(item.getEndTime()));
        String data = String.format(mContext.getResources().getString(R.string.work_submite_count), item.getSubmittedCount());
        holder.tvSubmitCount.setText(data);
        if (item.isOverdue()) {
            holder.ivOverdue.setVisibility(View.VISIBLE);
        } else {
            holder.ivOverdue.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        TextView     tvTitle;
        //        ImageView    ivJiaofu;
        //        ImageView    ivZhoulian;
        //        ImageView    ivJiaQi;
        LinearLayout llQuestionScore;
        TextView     tvStudentScore;
        TextView     tvTotalScore;
        LinearLayout llQuestionCount;
        LinearLayout llCorrecting;
        TextView     tvRightCount;
        TextView     tvWrongCount;
        //        TextView     tvDay;
        TextView     tvEndTime;
        TextView     tvSubmitCount;
        ImageView    ivOverdue;

        TextView correctingView;        //批阅中
    }

    public void setData(List<SubmitQuestion> questions) {
        mDatas.clear();
        mDatas.addAll(questions);
    }

}
